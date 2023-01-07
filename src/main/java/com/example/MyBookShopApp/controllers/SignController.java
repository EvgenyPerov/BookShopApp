package com.example.MyBookShopApp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SignController {

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
