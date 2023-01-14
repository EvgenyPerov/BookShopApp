package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.data.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@Controller
public class MainPageController {

    private BookService bookService;

    @Autowired
    public MainPageController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("recommendedBooks")
    public List<Book> recommendedBooks(){
        System.out.println("Количество рекомендуемых книг = " + bookService.getBooksData().size());
        return bookService.getBooksData();
    }

    @GetMapping("/")
    public String mainPage(){
        System.out.println("Переход на страницу Главная");
        return "/index";
    }

}
