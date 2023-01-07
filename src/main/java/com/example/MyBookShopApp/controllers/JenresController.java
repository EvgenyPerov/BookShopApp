package com.example.MyBookShopApp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class JenresController {

    @GetMapping("/genres")
    public String genrePage(){
        System.out.println("Переход на страницу Жанры");
        return "/genres/index";
    }

}
