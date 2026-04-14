package com.bookmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bookmall.service.CategoryService;
import com.bookmall.entity.Category;
import java.util.List;

@RestController
@RequestMapping("/api/categories") // 注意：這裡沒有 /admin
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 使用者獲取所有分類，用於首頁側邊欄或選單
     * 此 API 應在 SecurityConfig 設為 permitAll()
     */
    @GetMapping
    public ResponseEntity<List<Category>> listCategories() {
        // 直接調用現有的 Service 即可
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
}