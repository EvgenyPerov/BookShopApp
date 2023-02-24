package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.*;
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

    private BookService bookService;
    private OtherService otherService;

    @Autowired
    public MainPageController(BookService bookService, OtherService otherService) {
        this.bookService = bookService;
        this.otherService = otherService;
    }

    private SearchWordDto newSearchWord = new SearchWordDto();
    private boolean flagNewSearch = false;

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

    @ModelAttribute("searchWordDto")
    public SearchWordDto getSearchWord(){
        return new SearchWordDto();
    }

    @ModelAttribute("tagsMap")
    public Map<Tag, Integer> getTagsIdList(){
        return otherService.getTagsAndSizesMap();
    }

    @GetMapping(value = {"/search", "/", "/index"})
    public String mainPage(){
        System.out.println("Переход на страницу Главная");
        return "/index";
    }

    @GetMapping(value = { "/search/{searchWord}"})
    public String getSearchResults(@PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto,
                                   Model model) {
        SearchWordDto correctSearchWordDto;
        if (flagNewSearch) {
            correctSearchWordDto = newSearchWord;
        }
           else {
            correctSearchWordDto = searchWordDto;
        }

        model.addAttribute("searchWordDto", correctSearchWordDto);
        model.addAttribute("searchResults",
                bookService.getPageOfSearchResultBooks(correctSearchWordDto.getExample(), 0, 10).getContent());
        System.out.println("Переход на страницу поиска по слову " + correctSearchWordDto.getExample()+ " FLAG= "+ flagNewSearch);
        return "/search/index";
    }

    @GetMapping("/search/page/{searchWord}")
    @ResponseBody
    public BooksPageDto getNextSearchPage(Model model,
                                          @RequestParam("offset") Integer offset, @RequestParam("limit") Integer limit
                                          ,@PathVariable(value = "searchWord", required = false) SearchWordDto searchWordDto
                                          ){
//        model.addAttribute("searchWordDto", this.searchWord);
        if (offset >= 0) {
            flagNewSearch = true;
            newSearchWord.setExample(searchWordDto.getExample());
        }
        System.out.println("Переход на страницу внутреннего поиска \"/search/page/{searchWord}\" " + searchWordDto.getExample()+" offset= "+ offset);
        return new BooksPageDto(bookService.getPageOfSearchResultBooks(searchWordDto.getExample(), offset, limit).getContent());
    }

//    @GetMapping("/tags/index")
//    public String getTags(){
//        return "/tags/index";
//    }
}
