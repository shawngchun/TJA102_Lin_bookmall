package com.bookmall.controller;

import com.bookmall.entity.Book;
import com.bookmall.service.AdminBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/books")
@PreAuthorize("hasRole('ADMIN')") // 關鍵：只有管理員能進來
public class AdminBookController {

    @Autowired
    private AdminBookService adminBookService;

    // 1. 列表
    @GetMapping
    public ResponseEntity<List<Book>> listBooks() {
        return ResponseEntity.ok(adminBookService.getAllBooks());
    }

    // 2. 新增 (POST)
    @PostMapping
    public ResponseEntity<Book> create(@RequestBody Book book) {
        return ResponseEntity.ok(adminBookService.addBook(book));
    }

    // 3. 修改詳細資訊 (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<Book> update(@PathVariable Integer id, @RequestBody Book book) {
        return ResponseEntity.ok(adminBookService.updateBook(id, book));
    }

    // 4. 僅調整庫存 (PATCH)
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Book> adjustStock(@PathVariable Integer id, @RequestBody Map<String, Integer> payload) {
        Integer stock = payload.get("stock");
        return ResponseEntity.ok(adminBookService.updateStock(id, stock));
    }

    // 5. 刪除 (DELETE)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> remove(@PathVariable Integer id) {
        adminBookService.deleteBook(id);
        return ResponseEntity.ok(Map.of("message", "書籍已成功刪除"));
    }
}