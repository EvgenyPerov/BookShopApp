package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.BookService;
import com.example.MyBookShopApp.struct.book.book.Book;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(description = "book data api")
@RestController
@RequestMapping("/api/books")
public class BooksRestApiController {

    private BookService bookService;

    public BooksRestApiController(BookService bookService) {
        this.bookService = bookService;
    }

    @ApiOperation("operation to get books by author name")
    @GetMapping("/by-author")
    public ResponseEntity<List<Book>> booksByAuthor(@RequestParam("author") String name){
        return ResponseEntity.ok(bookService.getBooksByAuthor(name));
    }

    @ApiOperation("operation to get books by title")
    @GetMapping("/by-title")
    public ResponseEntity<List<Book>> booksByTitle(@RequestParam("title") String title){
        return ResponseEntity.ok(bookService.getBooksByTitle(title));
    }

    @ApiOperation("operation to get books by price-range")
    @GetMapping(value = "/by-price-range")
    public ResponseEntity<List<Book>> priceRangeBooks(@RequestParam("min") Double min, @RequestParam("max")Double max){
        return ResponseEntity.ok(bookService.getBooksWithPriceBetween(min, max));
    }

    @ApiOperation("operation to get books by price")
    @GetMapping("/by-price")
    public ResponseEntity<List<Book>> priceBooks(@RequestParam("price") Double price){
        return ResponseEntity.ok(bookService.getBooksWithPrice(price));
    }

    @ApiOperation("operation to get books with-max-discount")
    @GetMapping("/with-max-discount")
    public ResponseEntity<List<Book>> maxPriceBooks(){
        return ResponseEntity.ok(bookService.getBooksWithMaxDiscount());
    }

    @ApiOperation("operation to get books bestsellers (which is bestsellers = 1 (true)")
    @GetMapping("/bestsellers")
    public ResponseEntity<List<Book>> bestsellerBooks(){
        return ResponseEntity.ok(bookService.getBestsellers());
    }
}
