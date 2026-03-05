package com.bookmall.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.bookmall.service.BookService;
import com.bookmall.repository.BookRepository;
import com.bookmall.entity.Book;
import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

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
    public List<Book> searchBooks(String keyword) {
        return bookRepository.findByTitleContaining(keyword);
    }

    @Override
    public List<Book> getBooksByCategory(Integer categoryId) {
        return bookRepository.findByCategoryId(categoryId);
    }
}