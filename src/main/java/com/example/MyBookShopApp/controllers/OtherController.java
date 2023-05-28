package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.services.OtherService;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.data.services.UserService;
import com.example.MyBookShopApp.struct.book.book.Book;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class OtherController {

    private OtherService otherService;
    private final UserService userService;

    @Autowired
    public OtherController(OtherService otherService, UserService userService) {
        this.otherService = otherService;
        this.userService = userService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto(){
        return new SearchWordDto();
    }

    @ModelAttribute("status")
    public String authenticationStatus(){
        return (userService.getCurrentUser() == null)? "unauthorized" : "authorized";
    }

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

    @ApiOperation("этот метод изначально при переходе с главной страницы на Тэг")
    @GetMapping("/tags/index/{tagId}")
    public String getTags(Model model,
                          @PathVariable(value = "tagId", required = false) Integer tagId) {
        System.out.println("Переход на страницу тэгов");
        Page<Book> page = otherService.getPageOfTagBooks(tagId, 0, 5);
        model.addAttribute("bookListByTag",page.getContent());
        model.addAttribute("tagName",otherService.getTagNameById(tagId));
        model.addAttribute("id",tagId);
    return "/tags/index";
    }

    @ApiOperation("этот метод постраничная загрузка книг найденных по Тэгу")
    @ResponseBody
    @GetMapping("/books/tag/{tagId}")
    public BooksPageDto getRecentNextPage(@PathVariable(value = "tagId", required = false) Integer tagId
            ,@RequestParam(value ="offset", required = false) Integer offset
            , @RequestParam(value ="limit", required = false) Integer limit){
        Page<Book> page = otherService.getPageOfTagBooks(tagId, offset, limit);
        System.out.println("Передача данных популярных книг в JS с параметром offset = "+ offset + " limit= "+ limit);
        return new BooksPageDto(page.getContent());
    }

}
