package com.bookmall.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookmall.service.OrderService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Autowired private OrderService orderService;

    @PostMapping("/callback")
    public String handleEcpayCallback(@RequestParam Map<String, String> params) {
        // 1. 這裡務必驗證 params 裡的 CheckMacValue 是否正確 (安全性核心！)
        // 2. 判斷 RtnCode 是否為 1 (1 代表成功)
        if ("1".equals(params.get("RtnCode"))) {
            String tradeNo = params.get("MerchantTradeNo"); // 格式：BKML{orderId}T...
            Integer orderId = extractOrderId(tradeNo);
            
            // 呼叫原本 Service 的 payOrder(orderId) 更新狀態為 1
            orderService.payOrder(orderId);
            
            return "1|OK"; // 告訴綠界：我收到了，別再傳了
        }
        return "0|Fail";
    }
    
    private Integer extractOrderId(String tradeNo) {
        // 解析 "BKML15T..." 拿到 15
        return Integer.parseInt(tradeNo.substring(4, tradeNo.indexOf("T")));
    }
}