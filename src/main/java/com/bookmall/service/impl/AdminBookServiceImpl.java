package com.bookmall.service.impl;

import com.bookmall.entity.Book;
import com.bookmall.repository.BookRepository;
import com.bookmall.service.AdminBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminBookServiceImpl implements AdminBookService {

    @Autowired
    private BookRepository bookRepository;

    @Override
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
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
}