package com.bookmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.bookmall.service.CategoryService;
import com.bookmall.entity.Category;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/categories") // 改為 RESTful 風格的 API 路徑
public class AdminCategoryController {

    @Autowired
    private CategoryService categoryService; //

    // 1. 取得所有分類 (對應原本的 listCategories)
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories()); //
    }

    // 2. 新增分類 (對應原本的 saveCategory)
    // 注意：這裡改用 @RequestBody 接收 JSON 數據
    @PostMapping
    public ResponseEntity<?> saveCategory(@RequestBody Category category) {
        categoryService.saveCategory(category); //
        return ResponseEntity.ok(Map.of("message", "分類儲存成功"));
    }

    // 3. 刪除分類 (新增的功能，對應 Service 中的 deleteCategory)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id); //
        return ResponseEntity.ok(Map.of("message", "分類已刪除"));
    }
}