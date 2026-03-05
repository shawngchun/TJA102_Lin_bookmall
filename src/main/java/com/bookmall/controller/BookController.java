package com.bookmall.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.bookmall.service.BookService;
import com.bookmall.entity.Book;
import java.util.List;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    /**
     * 首頁：顯示所有書籍
     */
    @GetMapping("/")
    public String index(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("bookList", books);
        // 回傳 templates/index.html
        return "index"; 
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
        return "index";
    }
}