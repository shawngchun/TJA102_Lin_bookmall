package com.bookmall.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bookmall.entity.Book;
import com.bookmall.entity.Category;
import com.bookmall.service.BookService;
import com.bookmall.service.CategoryService;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;
    
    @Autowired
    private CategoryService categoryService; // 注入 CategoryService

    /**
     * 獲取所有書籍清單
     * GET http://localhost:8080/api/books
     */
    @GetMapping
    public ResponseEntity<?> getAllBooks() {
        List<Book> books = bookService.getAllBooks();
        
        // 偵錯用：在控制台印出數量
        System.out.println("Debug: 找到 " + (books != null ? books.size() : 0) + " 本書");

        if (books == null || books.isEmpty()) {
            // 這裡回傳的是 String，因為有 <?> 所以不會報錯
            return ResponseEntity.ok("目前資料庫中沒有書籍資料");
        }

        // 這裡回傳的是 List<Book>
        return ResponseEntity.ok(books);
    }

    /**
     * 獲取單一書籍詳情
     * GET http://localhost:8080/api/books/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookDetail(@PathVariable("id") Integer id) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(Map.of("message", "找不到該書籍"));
        }
        return ResponseEntity.ok(book);
    }

    /**
     * 搜尋功能
     * GET http://localhost:8080/api/books/search?keyword=Java
     */
    @GetMapping("/search")
    public ResponseEntity<List<Book>> search(@RequestParam(value = "keyword", required = false) String keyword) {
        List<Book> searchResults = bookService.searchBooks(keyword);
        return ResponseEntity.ok(searchResults);
    }
    
    /**
     * 獲取所有分類 (用於新增/修改書籍時的下拉選單資料)
     * GET http://localhost:8080/api/books/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    /**
     * 新增或更新書籍
     * POST http://localhost:8080/api/books/admin/save
     */
    @PostMapping("/admin/save")
    public ResponseEntity<?> saveBook(@RequestBody Book book) {
        try {
            bookService.saveBook(book);
            return ResponseEntity.status(HttpStatus.CREATED)
                                 .body(Map.of("message", "書籍儲存成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                                 .body(Map.of("message", "儲存失敗：" + e.getMessage()));
        }
    }

    /**
     * 刪除書籍
     * DELETE http://localhost:8080/api/books/admin/{id}
     */
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable Integer id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(Map.of("message", "書籍已成功刪除"));
    }
}