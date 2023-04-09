package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.services.ResourceStorage;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.data.services.UserService;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.struct.user.UserEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@Api(description = " entity book")
@Controller
@RequestMapping("/books")
public class BooksController {

    private BookService bookService;
    private ResourceStorage storage;

    private UserService userService;

    @Autowired
    public BooksController(BookService bookService, ResourceStorage storage, UserService userService) {
        this.bookService = bookService;
        this.storage = storage;
        this.userService = userService;
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
    public BooksPageDto getRecentNextPage(
              @RequestParam(value = "from", required = false) String from
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

    @ApiOperation("operation to get SLUG books")
    @GetMapping("/{bookId}")
    public String getSlugOfBook(Model model,
                                @PathVariable(value = "bookId", required = false) Integer bookId){
        System.out.println("Переход на страницу SLUG book Id = " + bookId);
        Book book =  bookService.getBookById(bookId);
        model.addAttribute("slugBook", book );

        System.out.println("Средний рейтинг = " + userService.getRatingMapByBookId(bookId).get(100));

        System.out.println("Количество голосов = " + userService.getRatingMapByBookId(bookId).get(200));

        model.addAttribute("ratingMap", userService.getRatingMapByBookId(bookId));

        model.addAttribute("reviewMap", userService.getReviewMapByBookId(bookId));

        model.addAttribute("df", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

//        model.addAttribute("reviewLikeRating", userService.getReviewLikeByReviewId(bookId));

        return "/books/slug";
    }

    @PostMapping("/{bookId}/img/save")
    public String saveNewBookImage(@RequestParam("file") MultipartFile file,
                                   @PathVariable(value = "bookId", required = false) Integer bookId ){
        System.out.println("Переход на страницу выбора обложки для книги");
        String savePath = storage.saveNewBookImage(file, bookService.getBookSlugById(bookId));
        Book book =  bookService.getBookById(bookId);
        book.setImage(savePath);
        bookService.update(book);
        System.out.println("Обновлена обложка для книги = " + book.getTitle());

        return "redirect:/books/" + bookId;
    }

    @ApiOperation("operation to download books")
    @GetMapping("/download/{hash}")
    public ResponseEntity<ByteArrayResource> getSlugOfBook(Model model,
                                                           @PathVariable(value = "hash", required = false) String hash){
        System.out.println("Переход на страницу download = " + hash);

        Path path = storage.getBookPathFile(hash);

        MediaType mediaType = storage.getBookFileMime(hash);

        byte[] data = storage.getBookFileByteArray(hash);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + path.getFileName().toString())
                .contentType(mediaType)
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }

@PostMapping("/changeBookStatus/rating/{bookId}")
    public String handleChangeBookStatusRating(@PathVariable(value = "bookId", required = true) Integer bookId,
                                        @RequestParam(value = "value", required = false) Integer value) {

        System.out.println("Выставлен для книги = " + bookId + " рейтинг = " + value);

        String hashUser = "hash55555";
        UserEntity user = userService.getUserByHash(hashUser);

        userService.setRatingForBook(bookService.getBookById(bookId), user, value);

        return "redirect:/books/" + bookId;
    }


    @PostMapping("/rateBookReview/{reviewId}/{value}")
    public String handleChangeBookStatusReview(@PathVariable(value = "reviewId", required = true) Integer reviewId,
                                               @PathVariable(value = "value", required = true) short value) {

        String hashUser = "hash55555";
        UserEntity user = userService.getUserByHash(hashUser);

        System.out.println("Лайки для отзыва = " + reviewId + " оценка = " + value);

        userService.setReviewLikeForBook(reviewId, user, value);

        return "redirect:/books/" + userService.getBookByReviewId(reviewId);
    }



}
