package com.bookmall.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.bookmall.entity.Book;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {
    
    // Spring Data JPA 會根據方法名稱自動解析為：
    // SELECT * FROM books WHERE category_id = ?
    List<Book> findByCategoryId(Integer categoryId);

    // 模糊查詢書名：SELECT * FROM books WHERE title LIKE %?%
    List<Book> findByTitleContaining(String keyword);
}