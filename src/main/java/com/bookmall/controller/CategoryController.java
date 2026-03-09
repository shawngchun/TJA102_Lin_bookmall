package com.bookmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.bookmall.service.CategoryService;
import com.bookmall.entity.Category;

@Controller
@RequestMapping("/admin/categories") // 統一的前綴路徑
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // 顯示清單
    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "category_list";
    }

    // 顯示新增表單
    @GetMapping("/new")
    public String showForm(Model model) {
        model.addAttribute("category", new Category());
        return "category_form";
    }

    // 儲存
    @PostMapping("/save")
    public String saveCategory(@ModelAttribute("category") Category category) {
        categoryService.saveCategory(category);
        return "redirect:/admin/categories";
    }
}