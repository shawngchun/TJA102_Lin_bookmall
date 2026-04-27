package com.bookmall.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookmall.dto.CartItemDto;
import com.bookmall.dto.CheckoutRequest;
import com.bookmall.dto.OrderResponseDto;
import com.bookmall.entity.BkmlUser;
import com.bookmall.entity.Book;
import com.bookmall.entity.Order;
import com.bookmall.entity.OrderItem;
import com.bookmall.repository.BkmlUserRepository;
import com.bookmall.repository.BookRepository;
import com.bookmall.repository.OrderRepository;
import com.bookmall.service.CartService;
import com.bookmall.service.OrderService;
import com.bookmall.utils.EcpayUtils;

@Service
public class OrderServiceImpl implements OrderService {
	
	// 注入 application.properties 裡的網址
    @Value("${ecpay.return-url}")
    private String ecpayReturnUrl;

    @Value("${ecpay.client-back-url}")
    private String ecpayClientBackUrl;
    
    // 綠界測試環境參數
    @Value("${ecpay.merchantID}")
    private String merchantID;
    @Value("${ecpay.hashKey}")
    private String hashKey;
    @Value("${ecpay.hashIV}")
    private String hashIV;
        

    @Autowired private CartService cartService;
    @Autowired private OrderRepository orderRepository;
    @Autowired private BkmlUserRepository userRepository;
    @Autowired private BookRepository bookRepository;

    @Override
    @Transactional // 事務現在在這裡，保護整個業務流程
    public Order checkout(String email, CheckoutRequest request) {
        // 1. 取得用戶
        BkmlUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));

        // 2. 取得並檢查購物車
        List<CartItemDto> cartItems = cartService.getCartDetails(email);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("購物車是空的");
        }

        Map<Integer, Book> bookCache = new HashMap<>();
        
        // 3. 檢查並扣除庫存 (原本 Controller 的 loop)
        for (CartItemDto item : cartItems) {
            Book book = bookRepository.findById(item.getBookId())
                    .orElseThrow(() -> new RuntimeException("找不到書籍 ID: " + item.getBookId()));
            if (book.getStock() < item.getQuantity()) {
                throw new RuntimeException("書籍 [" + book.getTitle() + "] 庫存不足");
            }
            book.setStock(book.getStock() - item.getQuantity());
            bookRepository.save(book);
            
            bookCache.put(book.getId(), book);
        }

        // 4. 建立訂單與明細 (原本 Controller 的 Lambda 轉換)
        Order order = new Order();
        order.setUserId(user.getId());
        order.setReceiverName(request.getReceiverName());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setStatus(0);

        BigDecimal totalAmount = cartItems.stream()
                .map(CartItemDto::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);

        List<OrderItem> orderItems = cartItems.stream().map(item -> {
            Book cachedBook = bookCache.get(item.getBookId()); // 從我們建立的 Map 中取得
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(cachedBook); // 直接關聯實體物件，JPA 會自動處理外鍵
            orderItem.setQuantity(item.getQuantity());
            orderItem.setCurrentPrice(item.getPrice());
            return orderItem;
        }).collect(Collectors.toList());

        order.setItems(orderItems);

        // 5. 儲存並清空
        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(email);
        
        return savedOrder;
    }

    @Override
    @Transactional
    public void payOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("訂單不存在"));
        if (order.getStatus() != 0) {
            throw new RuntimeException("訂單狀態不符，無法付款");
        }
        order.setStatus(1);
        orderRepository.save(order);
    }

    @Override
    public List<Order> getOrdersByEmail(String email) {
        // 1. 透過 email 找到用戶
        BkmlUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));

        // 2. 回傳該用戶的訂單清單
        return orderRepository.findAllByUserIdOrderByIdDesc(user.getId());
    }
    
    @Override
    public OrderResponseDto getLatestOrdersByEmail(String email) {
    	// 1. 透過 email 找到用戶
        BkmlUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));
        
        // 2. 獲取該用戶最新的一筆訂單
        Order order = orderRepository.findFirstByUserIdOrderByIdDesc(user.getId())
                .orElseThrow(() -> new RuntimeException("該用戶尚無訂單紀錄"));
        
        // 3. 不論狀態是 1 (成功) 還是 0 (待付款/失敗)，都轉換為 DTO
        OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderId(order.getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus()); // 這裡傳回狀態值
        dto.setMerchantTradeNo(order.getMerchantTradeNo());
        dto.setCreatedAt(order.getCreatedAt());

        // 4. 處理訂單明細轉換
        if (order.getItems() != null) {
            List<OrderResponseDto.OrderItemDto> itemDtos = order.getItems().stream().map(item -> {
                OrderResponseDto.OrderItemDto itemDto = new OrderResponseDto.OrderItemDto();
                itemDto.setBookId(item.getBook().getId());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setCurrentPrice(item.getCurrentPrice());
                return itemDto;
            }).collect(Collectors.toList());
            dto.setItems(itemDtos);
        }
        
        return dto;
    	}
    
    @Override
    public OrderResponseDto findOrderByIdAndUserId(Integer orderId, String email) {
    	// 1. 透過 email 找到用戶
        Integer userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("用戶不存在")).getId();
        
        // 2. 獲取該用戶最新的一筆訂單
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new RuntimeException("該用戶尚無訂單紀錄"));
        
        // 3. 不論狀態是 1 (成功) 還是 0 (待付款/失敗)，都轉換為 DTO
        OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderId(order.getId());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus()); // 這裡傳回狀態值
        dto.setMerchantTradeNo(order.getMerchantTradeNo());
        dto.setCreatedAt(order.getCreatedAt());

        // 4. 處理訂單明細轉換
        if (order.getItems() != null) {
            List<OrderResponseDto.OrderItemDto> itemDtos = order.getItems().stream().map(item -> {
                OrderResponseDto.OrderItemDto itemDto = new OrderResponseDto.OrderItemDto();
                itemDto.setBookId(item.getBook().getId());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setCurrentPrice(item.getCurrentPrice());
                return itemDto;
            }).collect(Collectors.toList());
            dto.setItems(itemDtos);
        }
        
        return dto;
    }
    	
    @Override
    public String generatePaymentForm(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("訂單不存在"));
        
        String tradeNo = "BKML" + orderId + "T" + System.currentTimeMillis()/1000; // 產生唯一訂單編號
        order.setMerchantTradeNo(tradeNo.toString());
        order = orderRepository.save(order);
        
        String tradeDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date());

        Map<String, String> params = new HashMap<>();
        params.put("MerchantID", merchantID);
        params.put("MerchantTradeNo", tradeNo);
        params.put("MerchantTradeDate", tradeDate);
        params.put("PaymentType", "aio");
        params.put("TotalAmount", String.valueOf(order.getTotalAmount().intValue())); // 綠界只收整數
        params.put("TradeDesc", "BookMall 訂單付款");
        params.put("ItemName", "網路書店書籍一批");
        params.put("ReturnURL", ecpayReturnUrl); // 綠界通知後端的地方
        params.put("OrderResultURL", ecpayClientBackUrl); // 使用者付完後跳回的地方
        params.put("ChoosePayment", "ALL");
        params.put("EncryptType", "1");

        // 使用我們先前討論的 EcpayUtils 計算簽章
        String checkMacValue = EcpayUtils.generateCheckMacValue(params, hashKey, hashIV);
        params.put("CheckMacValue", checkMacValue);

        // 產生一個隱藏表單，載入後自動 submit 跳轉到綠界
        return buildAutoPostForm(params);
    }

    // private 僅提供給 OrderServiceImpl 使用的內部方法
    private String buildAutoPostForm(Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<form id='ecpayForm' action='https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5' method='post'>");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            sb.append("<input type='hidden' name='").append(entry.getKey()).append("' value='").append(entry.getValue()).append("'>");
        }
        sb.append("</form><script>document.getElementById('ecpayForm').submit();</script>");
        System.out.println(sb.toString());
        return sb.toString();
    }

}