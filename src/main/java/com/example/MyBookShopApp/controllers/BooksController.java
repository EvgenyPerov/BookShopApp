package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.BooksPageDto;
import com.example.MyBookShopApp.data.SearchWordDto;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.data.BookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Api(description = " entity book")
@Controller
@RequestMapping("/books")
public class BooksController {

    private BookService bookService;

    @Autowired
    public BooksController(BookService bookService) {
        this.bookService = bookService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto(){
        return new SearchWordDto();
    }

    @ModelAttribute("recentResults")
    public List<Book> recentResults(){
        return new ArrayList<>();
    }

    // метод срабатывает первым при выборе Новинок в главном меню
    @ApiOperation("operation to get recent books")
    @GetMapping("/recent")
    public String recentFirstPage(Model model
            , @RequestParam(value = "from", required = false) String from
            , @RequestParam(value ="to", required = false) String to
            , @RequestParam(value ="offset", required = false) Integer offset
            , @RequestParam(value ="limit", required = false) Integer limit) {

        System.out.println("Переход на страницу Новинки c параметрами: " +
                "from " + from +" to= "+ to + " offset= "+ offset + " limit= "+ limit);

        model.addAttribute("recentResults",
                bookService.getPageOfRecentBooks(from, to, 0, 5).getContent());
        return "/books/recent";
    }

    // метод срабатывает при "Показать еще" подгрузке следующей партии книг на
    @ApiOperation("этот метод мапинга нужен для JS файла, постраничная загрузка новинок книг")
    @ResponseBody
    @GetMapping("/page/recent")
    public BooksPageDto getRecentNextPage( @RequestParam(value = "from", required = false) String from
            , @RequestParam(value ="to", required = false) String to
            , @RequestParam(value ="offset", required = false) Integer offset
            , @RequestParam(value ="limit", required = false) Integer limit) {
        return  new BooksPageDto(bookService.getPageOfRecentBooks(from, to, offset, limit).getContent());
    }

    // метод срабатывает выборе в главном меню
    @ApiOperation("operation to get popular books")
    @GetMapping("/popular")
    public String popularPage(Model model
            ,@RequestParam(value ="offset", required = false) Integer offset
            , @RequestParam(value ="limit", required = false) Integer limit){
        System.out.println("Переход на страницу Популярное");
        model.addAttribute("popularBooks",
                bookService.getPageOfPopularBooks( 0, 5).getContent());
        return "/books/popular";
    }

    // метод срабатывает при карусельной подгрузке следующей партии книг на главной странице
    @ApiOperation("этот метод мапинга нужен для JS файла, постраничная загрузка популярных книг")
    @ResponseBody
    @GetMapping("/page/popular")
    public BooksPageDto getPopularBooksPage(@RequestParam("offset") Integer offset,
                                            @RequestParam("limit") Integer limit) {
        System.out.println("Передача данных популярных книг в JS с параметром offset = "+ offset + " limit= "+ limit);
        return new BooksPageDto(bookService.getPageOfPopularBooks(offset,limit).getContent());
    }

    // метод срабатывает при карусельной подгрузке следующей партии книг на главной странице
    @ApiOperation("этот метод мапинга нужен для JS файла, постраничная загрузка рекомендованных книг")
    @ResponseBody
    @GetMapping("/page/recommended")
    public BooksPageDto getRecommendedeBooksPage(@RequestParam("offset") Integer offset,
                                                 @RequestParam("limit") Integer limit) {
        System.out.println("Передача данных рекомендованных книг в JS");
        return new BooksPageDto(bookService.geRecomendedBooksOnMainPage(offset,limit).getContent());
    }

}
