package com.bookmall.service;

import java.util.List;
import com.bookmall.entity.Category;

public interface CategoryService {
    List<Category> getAllCategories();
    void saveCategory(Category category);
    Category getCategoryById(Integer id);
    void deleteCategory(Integer id);
}