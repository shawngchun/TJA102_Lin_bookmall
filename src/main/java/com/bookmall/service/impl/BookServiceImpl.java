package com.bookmall.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bookmall.dto.BookListDTO;
import com.bookmall.entity.Book;
import com.bookmall.repository.BookRepository;
import com.bookmall.service.BookService;

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
    public List<BookListDTO> getBooksByCategory(Integer categoryId) {
        List<Book> books = bookRepository.findByCategoryId(categoryId);
        
        return books.stream()
        			.map(this::convertToDTO)
        			.collect(Collectors.toList());
    }
    
    @Override
    public void saveBook(Book book) {
        bookRepository.save(book);
    }

    @Override
    public void deleteBook(Integer id) {
        bookRepository.deleteById(id);
    }
    
    public List<BookListDTO> getAllBooksForClient() {
        List<Book> books = bookRepository.findAll();
        
        return books.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
    }

    // 將單個 Entity 轉為 DTO 的私有方法
    private BookListDTO convertToDTO(Book book) {
        BookListDTO dto = new BookListDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setPrice(book.getPrice());
        dto.setStock(book.getStock());
        dto.setPictureUrl(book.getPictureUrl());
        // 處理 null 安全性
        if (book.getCategory() != null) {
            dto.setCategoryName(book.getCategory().getName());
        } else {
            dto.setCategoryName("未分類");
        }
        return dto;
    }
}