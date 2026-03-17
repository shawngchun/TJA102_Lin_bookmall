package com.bookmall.service;

import java.util.List;

import com.bookmall.entity.Book;

public interface AdminBookService {
	// 取得所有書籍（包含庫存資訊）
    List<Book> getAllBooks();
    
    // 上架新書
    Book addBook(Book book);
    
    // 修改書籍詳細資訊
    Book updateBook(Integer id, Book bookDetails);
    
    // 調整庫存（補貨或手動修正）
    Book updateStock(Integer id, Integer newStock);
    
    // 下架/刪除書籍
    void deleteBook(Integer id);
}