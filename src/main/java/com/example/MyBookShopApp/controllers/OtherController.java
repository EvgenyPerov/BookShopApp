package com.example.MyBookShopApp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class OtherController {

    @GetMapping("/documents/index")
    public String documentsPage(){
        System.out.println("Переход на страницу Документы");
        return "/documents/index";
    }

    @GetMapping("/about")
    public String aboutCompanyPage(){
        System.out.println("Переход на страницу О компании");
        return "/about";
    }

    @GetMapping("/faq")
    public String faqPage(){
        System.out.println("Переход на страницу Помощь");
        return "/faq";
    }

    @GetMapping("/contacts")
    public String contactsPage(){
        System.out.println("Переход на страницу Контакты");
        return "/contacts";
    }
}
