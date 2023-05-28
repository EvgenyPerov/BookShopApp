package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.data.services.OtherService;
import com.example.MyBookShopApp.data.services.UserService;
import com.example.MyBookShopApp.errs.EmptySearchException;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.other.Tag;
import io.swagger.annotations.Api;
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

    @Autowired
    public MainPageController(BookService bookService, OtherService otherService, UserService userService) {
        this.bookService = bookService;
        this.otherService = otherService;
        this.userService = userService;
    }

    @ModelAttribute("recommendedBooks")
    public List<Book> recommendedBooks(){
        return bookService.geRecomendedBooksOnMainPage( 0,6).getContent();
    }

    @ModelAttribute("recentedBooks")
    public List<Book> recentedBooks(){
        return bookService.getRecentedBooksOnMainPage(0, 6).getContent();
    }

    @ModelAttribute("popularBooks")
    public List<Book> popularBooks(){
        return bookService.getPopularBooksOnMainPage(0,6).getContent();
    }

    @ModelAttribute("searchResults")
    public List<Book> searchResults(){
        return new ArrayList<>();
    }

    @ModelAttribute("status")
    public String authenticationStatus(){
        return (userService.getCurrentUser() == null)? "unauthorized" : "authorized";
    }

    @ModelAttribute("tagsMap")
    public Map<Tag, Integer> getTagsIdList(){
        return otherService.getTagsAndSizesMap();
    }

    @GetMapping(value = { "/", "/index"})
    public String mainPage(Model model){
        System.out.println("Переход на страницу Главная");
        model.addAttribute("searchWordDto", new SearchWordDto());
        return "/index";
    }

    @GetMapping(value = {"/search", "/search/{searchWord}"})
    public String getSearchResult(@PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto,
                                  Model model) throws EmptySearchException {
        if (searchWordDto != null) {
        model.addAttribute("searchWordDto", searchWordDto);
        List<Book> searchResults = bookService.getPageOfSearchResultBooks(searchWordDto.getExample(), 0, 20).getContent();
        model.addAttribute("searchResults",searchResults);
            System.out.println("найдено сначала книг = " + searchResults.size());
        return "/search/index";
        } else {
            throw new EmptySearchException("Поисковый запрос не задан");
        }
    }
    @GetMapping("/search/page/{searchWord}")
    @ResponseBody
    public BooksPageDto getNextSearchPage(Model model,
                                          @RequestParam("offset") Integer offset,
                                          @RequestParam("limit") Integer limit,
                                          @PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto) {
        List<Book> searchResults =bookService.getPageOfSearchResultBooks(searchWordDto.getExample(), offset, limit).getContent();
//            model.addAttribute("isEmptyList",searchResults.isEmpty());
        System.out.println("найдено Pageable книг = " + searchResults.size());
        return new BooksPageDto(searchResults);
    }


}
