package com.bookmall.entity;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 對應 MySQL 的 INT PRIMARY KEY

    @Column(nullable = false)
    private String name;

    // 雙向關聯：一個分類對應多本書
    // mappedBy 指向 Book 類別中定義 Category 的變數名稱
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Book> books;

    // JPA 規範要求的無參數建構子
    public Category() {
    }

    // --- Getter and Setter ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }
}