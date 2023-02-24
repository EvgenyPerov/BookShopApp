package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.SearchWordDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class SignController {

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto(){
        return new SearchWordDto();
    }

    @GetMapping("/signin")
    public String signInPage(){
        System.out.println("Переход на страницу Вход");
        return "/signin";
    }

    @GetMapping("/signup")
    public String signUpPage(){
        System.out.println("Переход на страницу Регистрация");
        return "/signup";
    }

}
