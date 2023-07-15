package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.data.services.Book2UserService;
import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.data.services.UserService;
import com.example.MyBookShopApp.struct.author.Author;
import com.example.MyBookShopApp.data.services.AuthorService;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.user.UserEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@ApiModel(description = "data model of authors entity")
@Api("authors data")
public class AuthorsController {
    private AuthorService authorService;
    private final UserService userService;

    private final BookService bookService;

    private final Book2UserService book2UserService;

    @Autowired
    public AuthorsController(AuthorService authorService, UserService userService, BookService bookService, Book2UserService book2UserService) {
        this.authorService = authorService;
        this.userService = userService;
        this.bookService = bookService;
        this.book2UserService = book2UserService;
    }

    @ModelAttribute("authorsMap")
    public Map<String, List<Author>> authorsMap(){
        return authorService.getAuthorsMap();
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto(){
        return new SearchWordDto();
    }

    @ModelAttribute("status")
    public String authenticationStatus(){
        return (userService.getCurrentUser() == null)? "unauthorized" : "authorized";
    }

    @ModelAttribute("bookPostponedList")
    public List<Book> geBookPostponedList(@CookieValue(name = "postponedContents", required = false) String postponedContents){
        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            return book2UserService.getBooksFromRepoByTypeCodeAndUser("KEPT", user);
        } else {
            return bookService.getBooksFromCookies(postponedContents);
        }
    }

    @ModelAttribute("bookCartList")
    public List<Book> geBookCartList(@CookieValue(name = "cartContents", required = false) String cartContents){
        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            return book2UserService.getBooksFromRepoByTypeCodeAndUser("CART",user);
        } else {
            return bookService.getBooksFromCookies(cartContents);
        }
    }

    @GetMapping("/authors")
    public String authorsPage(){
        System.out.println("Переход на страницу Авторы");
        System.out.println("Количество авторов = " + authorService.getAuthorsMap().size());
        return "/authors/index";
    }
    @ApiOperation("method to get map of authors")
    @GetMapping("/api/authors")
    @ResponseBody
    public Map<String, List<Author>> authors(){
        return authorService.getAuthorsMap();
    }


    @GetMapping("/authors/slug/{authorId}")
    public String getAuthors(Model model,
                                @PathVariable(value = "authorId", required = false) Integer authorId){
        Page<Book> page = authorService.getBooksByAuthorId(authorId, 0,10);
        System.out.println("Найдено книг автора = " + page.getContent().size());
        model.addAttribute("authorBooks",page.getContent());
        model.addAttribute("id",authorId);
        model.addAttribute("countOfBooks", authorService.getCountBooksByAuthorId(authorId));
        model.addAttribute("name",authorService.getAuthorNameById(authorId));
        return "/authors/slug";
    }

    @GetMapping("/books/author/{authorId}")
    public String getAuthorPage(Model model,
                             @PathVariable(value = "authorId", required = false) Integer authorId){
        Page<Book> page = authorService.getBooksByAuthorId(authorId, 0,5);
        System.out.println("Найдено дополнительно книг автора = " + page.getContent().size());
        model.addAttribute("authorBooks",page.getContent());
        model.addAttribute("id",authorId);
        model.addAttribute("name",authorService.getAuthorNameById(authorId));
        return "/books/author";
    }
    @ApiOperation("этот метод постраничная загрузка книг найденных по Автору")
    @ResponseBody
    @GetMapping("/books/page/author/{authorId}")
    public BooksPageDto getAuthorNextPages(
             @PathVariable(value = "authorId", required = false) Integer authorId
            , @RequestParam(value ="offset", required = false) Integer offset
            , @RequestParam(value ="limit", required = false) Integer limit){
        Page<Book> page = authorService.getBooksByAuthorId(authorId, offset, limit);
        return new BooksPageDto(page.getContent());
    }


}
