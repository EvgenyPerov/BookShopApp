package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.services.Book2UserService;
import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.data.services.GenreService;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.data.services.UserService;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.user.UserEntity;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class JenresController {

    private GenreService genreService;
    private final UserService userService;

    private final BookService bookService;
    private final Book2UserService book2UserService;

    @Autowired
    public JenresController(GenreService genreService, UserService userService, BookService bookService, Book2UserService book2UserService) {
        this.genreService = genreService;
        this.userService = userService;
        this.bookService = bookService;
        this.book2UserService = book2UserService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto(){
        return new SearchWordDto();
    }

    @ModelAttribute("state")
    public String authenticationState(){
        return (userService.getCurrentUser() == null)? "unauthorized" : "authorized";
    }

    @ModelAttribute("myBooks")
    public int geBookPostponedList() {
        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            return book2UserService.getBooksFromRepoByTypeCodeAndUser("PAID", user).size();
        } else {
            return 0;
        }
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

    @ApiOperation("этот метод изначально при переходе с главной страницы на Жанры")
    @GetMapping("/genres")
    public String getGenreList(Model model){
        System.out.println("Переход на страницу Жанры");
        model.addAttribute("genreList",genreService.getGenreList());
        return "/genres/index";
    }

    @GetMapping("/genres/slug/{jenreId}")
    public String getGenrePages(Model model,
                                @PathVariable(value = "jenreId", required = false) Integer jenreId){
        List<Book> list = genreService.getPageOfGenreBooks(jenreId, 0,20);
        model.addAttribute("jenrePagesBooks",list);
        model.addAttribute("id",jenreId);
        model.addAttribute("name",genreService.getGenreNameById(jenreId));
        return "/genres/slug";
    }

    @ApiOperation("этот метод постраничная загрузка книг найденных по Жанру")
    @ResponseBody
    @GetMapping("/books/genre/{jenreId}")
    public BooksPageDto getGenrePages(Model model
            ,@PathVariable(value = "jenreId", required = false) Integer jenreId
            ,@RequestParam(value ="offset", required = false) Integer offset
            , @RequestParam(value ="limit", required = false) Integer limit){
        List<Book> list = genreService.getPageOfGenreBooks(jenreId, offset, limit);
        return new BooksPageDto(list);
    }

}
