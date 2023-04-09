package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.services.GenreService;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.struct.book.book.Book;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class JenresController {

    private GenreService genreService;

    @Autowired
    public JenresController(GenreService genreService) {
        this.genreService = genreService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto(){
        return new SearchWordDto();
    }

    @ApiOperation("этот метод изначально при переходе с главной страницы на Жанры")
    @GetMapping("/genres")
    public String getGenreList(Model model){
        System.out.println("Переход на страницу Жанры");
        model.addAttribute("genreList",genreService.getGenreMap());
        return "/genres/index";
    }

    @GetMapping("/genres/slug/{jenreId}")
    public String getGenrePages(Model model,
                                @PathVariable(value = "jenreId", required = false) Integer jenreId){
        Page<Book> page = genreService.getPageOfGenreBooks(jenreId, 0,5);
        model.addAttribute("jenrePagesBooks",page.getContent());
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
        Page<Book> page = genreService.getPageOfGenreBooks(jenreId, offset, limit);
        return new BooksPageDto(page.getContent());
    }

}
