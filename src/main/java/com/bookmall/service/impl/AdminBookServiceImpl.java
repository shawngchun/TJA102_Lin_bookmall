package com.bookmall.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bookmall.entity.Book;
import com.bookmall.entity.Category;
import com.bookmall.repository.BookRepository;
import com.bookmall.repository.CategoryRepository;
import com.bookmall.service.AdminBookService;

@Service
public class AdminBookServiceImpl implements AdminBookService {

    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
    
    @Override
    public Book getBookById(Integer id) {
        // 使用 .orElse(null) 處理找不到書的情況
        return bookRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Book addBook(Book book) {
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book updateBook(Integer id, Book bookDetails) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到編號為 " + id + " 的書籍"));
        
        // 更新欄位
        book.setTitle(bookDetails.getTitle());
        book.setPrice(bookDetails.getPrice());
        book.setDescription(bookDetails.getDescription());
        book.setStock(bookDetails.getStock());
        // 如果有分類（Category），也要在此更新
        
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public Book updateStock(Integer id, Integer newStock) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("書籍不存在"));
        book.setStock(newStock);
        return bookRepository.save(book);
    }

    @Override
    @Transactional
    public void deleteBook(Integer id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("欲刪除的書籍不存在");
        }
        bookRepository.deleteById(id);
    }
    
    @Override
    @Transactional
    public Book partialUpdateBook(Integer id, Map<String, Object> updates) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("書籍不存在"));

        updates.forEach((key, value) -> {
            // 增加防呆：如果 value 為 null 或空字串則跳過，避免轉換失敗
            if (value == null || value.toString().trim().isEmpty()) return;

            switch (key) {
                case "title": book.setTitle(value.toString()); break;
                case "author": book.setAuthor(value.toString()); break;
                case "price": book.setPrice(new java.math.BigDecimal(value.toString())); break;
                case "stock": book.setStock(Integer.parseInt(value.toString())); break; // 改用解析
                case "description": book.setDescription(value.toString()); break;
                case "pictureUrl": book.setPictureUrl(value.toString()); break;
                case "categoryId": 
                    // 根據前端傳來的 ID 找到分類物件，並設定給 Book
                    Integer catId = Integer.parseInt(value.toString());
                    Category cat = categoryRepository.findById(catId)
                            .orElseThrow(() -> new RuntimeException("分類不存在"));
                    book.setCategory(cat);
                    break;
            }
        });

        return bookRepository.save(book);
    }
}