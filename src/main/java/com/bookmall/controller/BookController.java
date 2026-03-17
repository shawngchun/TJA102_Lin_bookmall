package com.bookmall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookmall.entity.Book;
import com.bookmall.service.BookService;
import com.bookmall.service.CategoryService;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;
    
    @Autowired
    private CategoryService categoryService; // 注入 CategoryService

    /**
     * 首頁：顯示所有書籍
     */
    @GetMapping("/booklist")
    public String listBook(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("bookList", books);
        // 回傳 templates/book_list.html
        return "book_list"; 
    }

    /**
     * 書籍詳情頁：使用路徑變數
     */
    @GetMapping("/book/{id}")
    public String bookDetail(@PathVariable("id") Integer id, Model model) {
        Book book = bookService.getBookById(id);
        if (book == null) {
            return "redirect:/"; // 如果找不到書，重導向回首頁
        }
        model.addAttribute("book", book);
        return "book_detail";
    }

    /**
     * 搜尋功能：接收查詢參數
     */
    @GetMapping("/search")
    public String search(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        List<Book> searchResults = bookService.searchBooks(keyword);
        model.addAttribute("bookList", searchResults);
        model.addAttribute("keyword", keyword);
        return "book_list";
    }
    
// // 顯示新增表單
//    @GetMapping("/admin/book/new")
//    public String showCreateForm(Model model) {
//        model.addAttribute("book", new Book());
//        // 這裡需要撈出所有分類供下拉選單選擇
//        model.addAttribute("categories", categoryService.getAllCategories());
//        return "book_form";
//    }
//
//    // 處理儲存動作
//    @PostMapping("/admin/book/save")
//    public String saveBook(Book book) {
//        bookService.saveBook(book);
//        return "redirect:/"; // 儲存完回首頁
//    }
//
//    // 刪除書籍
//    @GetMapping("/admin/book/delete/{id}")
//    public String deleteBook(@PathVariable Integer id) {
//        bookService.deleteBook(id);
//        return "redirect:/";
//    }
}