package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.data.services.Book2UserService;
import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.data.services.OtherService;
import com.example.MyBookShopApp.data.services.UserService;
import com.example.MyBookShopApp.errs.EmptySearchException;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.other.Tag;
import com.example.MyBookShopApp.struct.user.UserEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Api(description = "контроллер для обработки главной страницы book")
@Controller
public class MainPageController {

    private final BookService bookService;
    private final OtherService otherService;

    private final UserService userService;

    private final Book2UserService book2UserService;

    @Autowired
    public MainPageController(BookService bookService, OtherService otherService, UserService userService, Book2UserService book2UserService) {
        this.bookService = bookService;
        this.otherService = otherService;
        this.userService = userService;
        this.book2UserService = book2UserService;
    }

    @ModelAttribute("recentedBooks")
    public List<Book> recentedBooks(){
        return bookService.getPageOfRecentBooks(null,null, 0, 6);
    }

    @ModelAttribute("popularBooks")
    public List<Book> popularBooks(){
        return bookService.getPageOfPopularBooks(0,6);
    }

    @ModelAttribute("searchResults")
    public List<Book> searchResults(){
        return new ArrayList<>();
    }

    @ModelAttribute("state")
    public String authenticationState(){
        return (userService.getCurrentUser() == null)? "unauthorized" : "authorized";
    }

    @ModelAttribute("tagsMap")
    public Map<Tag, Integer> getTagsIdList(){
        return otherService.getTagsAndSizesMap();
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

    @GetMapping(value = {"/search", "/search/{searchWord}"})
    public String getSearchResult(@PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto,
                                  Model model) throws EmptySearchException {
        if (searchWordDto != null) {
        model.addAttribute("searchWordDto", searchWordDto);
//        List<Book> searchResults = bookService.getPageOfGoogleBooksApiSearchResult(searchWordDto.getExample(), 0, 20);
        List<Book> searchResults = bookService.getPageOfSearchResultBooks(searchWordDto.getExample(), 0, 20);
        model.addAttribute("searchResults",searchResults);
            System.out.println("найдено сначала книг = " + searchResults.size());
        return "/search/index";
        } else {
            throw new EmptySearchException("Поисковый запрос не задан");
        }
    }
    @GetMapping("/search/page/{searchWord}")
    @ResponseBody
    public BooksPageDto getNextSearchPage(@RequestParam("offset") Integer offset,
                                          @RequestParam("limit") Integer limit,
                                          @PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto,
                                          Model model) {
//        List<Book> searchResults =bookService.getPageOfGoogleBooksApiSearchResult(searchWordDto.getExample(), offset, limit);
        List<Book> searchResults =bookService.getPageOfSearchResultBooks(searchWordDto.getExample(), offset, limit);
        model.addAttribute("searchWordDto", searchWordDto);
        System.out.println("searchWordDto = " + searchWordDto.getExample()); //
        System.out.println("найдено Pageable книг = " + searchResults.size());
        return new BooksPageDto(searchResults);
    }

    @GetMapping(value = { "/", "/index"})
    public String mainPage(Model model,
                           @CookieValue(name = "cartContents", required = false) String cartContents,
                           @CookieValue(name = "postponedContents", required = false) String postponedContents){
        System.out.println("Переход на страницу Главная");
        model.addAttribute("searchWordDto", new SearchWordDto());
        model.addAttribute("recommendedBooks",
                bookService.getRecomendedBooksOnMainPage(postponedContents, cartContents, 0,6));
        return "/index";
    }

    // метод срабатывает при карусельной подгрузке следующей партии книг на главной странице
    @ApiOperation("этот метод мапинга нужен для JS файла, постраничная загрузка рекомендованных книг")
    @ResponseBody
    @GetMapping("/books/page/recommended")
    public BooksPageDto getRecommendedeBooksPage(@CookieValue(name = "cartContents", required = false) String cartContents,
                                                 @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                                 @RequestParam(value ="offset" , required = false) Integer offset,
                                                 @RequestParam(value ="limit" , required = false) Integer limit) {
        System.out.println("Передача данных рекомендованных книг в JS с параметрами offset = "+ offset + " limit= "+ limit);
        return new BooksPageDto(bookService.
                getRecomendedBooksOnMainPage(postponedContents, cartContents, offset,limit));
    }

}
