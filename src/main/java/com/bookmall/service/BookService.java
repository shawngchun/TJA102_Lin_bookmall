package com.bookmall.service;

import java.util.List;

import com.bookmall.dto.BookListDTO;
import com.bookmall.entity.Book;

public interface BookService {
    // 獲取所有書籍
    List<Book> getAllBooks();
    
    // 根據 ID 找書
    Book getBookById(Integer id);
    
    // 關鍵字搜尋
    List<Book> searchBooks(String keyword);
    
    // 根據分類找書
    List<Book> getBooksByCategory(Integer categoryId);
    
    List<BookListDTO> getAllBooksForClient();
    
    void saveBook(Book book);
    void deleteBook(Integer id);
}