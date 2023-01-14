package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.data.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BooksController {

    private BookService bookService;

    @Autowired
    public BooksController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("booksList")
    public List<Book> bookList(){
        return bookService.getBooksData();
    }

    @GetMapping("/recent")
    public String recentPage(){
        System.out.println("Переход на страницу Новинки");
        return "/books/recent";
    }

    @GetMapping("/popular")
    public String popularPage(){
        System.out.println("Переход на страницу Популярное");
        return "/books/popular";
    }


}
