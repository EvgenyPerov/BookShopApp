package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.services.Book2UserService;
import com.example.MyBookShopApp.data.services.ResourceStorage;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.data.services.UserService;
import com.example.MyBookShopApp.errs.BadRequestException;
import com.example.MyBookShopApp.errs.NotExistBookException;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.struct.user.UserEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Api(description = " entity book")
@Controller
@RequestMapping("/books")
public class BooksController {

    private BookService bookService;
    private ResourceStorage storage;
    private UserService userService;

    private final Book2UserService book2UserService;

    @Autowired
    public BooksController(BookService bookService, ResourceStorage storage, UserService userService, Book2UserService book2UserService) {
        this.bookService = bookService;
        this.storage = storage;
        this.userService = userService;
        this.book2UserService = book2UserService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto(){
        return new SearchWordDto();
    }

    @ModelAttribute("status")
    public String authenticationStatus(){
        return (userService.getCurrentUser() == null)? "unauthorized" : "authorized";
    }

    @ModelAttribute("recentResults")
    public List<Book> recentResults(){
        return new ArrayList<>();
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

    @ModelAttribute("lookedBooks")
    public List<Book> geBookLookedList() {
        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            return book2UserService.getLookedBooksByUserLastMonth(user);
        } else {
            return new ArrayList<>();
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

    // метод срабатывает первым при выборе Новинок в главном меню
    @ApiOperation("operation to get recent books")
    @GetMapping("/recent")
    public String recentFirstPage(Model model) {
        System.out.println("Переход на страницу Новинки");
        model.addAttribute("recentResults",
                bookService.getPageOfRecentBooks(null, null, 0, 20).getContent());
        return "/books/recent";
    }

    // метод срабатывает при "Показать еще" подгрузке следующей партии книг на Новинках +
    // метод срабатывает при карусельной подгрузке следующей партии книг на Главной странице
    @ApiOperation("этот метод мапинга нужен для JS файла, постраничная загрузка новинок книг")
    @ResponseBody
    @GetMapping("/page/recent")
    public BooksPageDto getRecentNextPage(
              @RequestParam(value = "from", required = false) String from
            , @RequestParam(value ="to", required = false) String to
            , @RequestParam(value ="offset", required = false) Integer offset
            , @RequestParam(value ="limit", required = false) Integer limit) {
        System.out.println("Передача данных Новинок книг в JS с параметрами offset = "+ offset +
                " limit= "+ limit + " from= "+ from + " to= "+ to);
        return new BooksPageDto(bookService.getPageOfRecentBooks(from, to, offset, limit).getContent());
    }

    // метод срабатывает выборе в главном меню
    @ApiOperation("operation to get popular books")
    @GetMapping("/popular")
    public String popularPage(Model model){
        System.out.println("Переход на страницу Популярное");
        model.addAttribute("popularBooks",bookService.getPageOfPopularBooks(0, 20).getContent());
        return "/books/popular";
    }

    // метод срабатывает при карусельной подгрузке следующей партии книг на главной странице
    @ApiOperation("этот метод мапинга нужен для JS файла, постраничная загрузка популярных книг")
    @ResponseBody
    @GetMapping("/page/popular")
    public BooksPageDto getPopularBooksPage(@RequestParam("offset") Integer offset,
                                            @RequestParam("limit") Integer limit) {
        System.out.println("Передача данных популярных книг в JS с параметрами offset = "+ offset + " limit= "+ limit);
        return new BooksPageDto(bookService.getPageOfPopularBooks(offset,limit).getContent());
    }


    @ApiOperation("operation to get SLUG books")
    @GetMapping("/{bookId}")
    public String getSlugOfBook(Model model,
                                @PathVariable(value = "bookId", required = false) Integer bookId) throws Exception {
        System.out.println("Переход на страницу SLUG book Id = " + bookId + " Тип- "+bookId.getClass());
        try {
            Book book = bookService.getBookById(bookId);
            model.addAttribute("slugBook", book);

            System.out.println("Средний рейтинг = " + userService.getRatingMapByBookId(bookId).get(100));

            System.out.println("Количество голосов = " + userService.getRatingMapByBookId(bookId).get(200));

            model.addAttribute("ratingMap", userService.getRatingMapByBookId(bookId));

            model.addAttribute("reviewMap", userService.getReviewMapByBookId(bookId));

            model.addAttribute("df", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

            UserEntity user = userService.getCurrentUser();
            if (user != null) {
                book2UserService.addLookedBook(book, user);
                model.addAttribute("curUser", user);
                return "/books/slugmy";
            }

            return "/books/slug";

        } catch (TypeMismatchException e){ //MethodArgumentTypeMismatchException NumberFormatException TypeMismatchException
            System.out.println("bookController поймал ошибку NumberFormatException");
            throw new BadRequestException("Введен недопустимый формат данных");
        } catch (NoSuchElementException e){
            System.out.println("bookController поймал ошибку NoSuchElementException");
            throw new NotExistBookException("Не существует книги с таким номером");
        }
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
    public ResponseEntity<ByteArrayResource> getSlugOfBook(@PathVariable(value = "hash", required = false) String hash){
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
                                        @RequestParam(value = "value", required = false) int value) {

    if (authenticationStatus() == "authorized"){
        UserEntity user = userService.getCurrentUser();
        Book book = bookService.getBookById(bookId);
        userService.setRatingForBook(book, user, value);
    }
    return "redirect:/books/" + bookId;
    }


    @PostMapping("/rateBookReview/{reviewId}/{value}")
    public String handleChangeBookStatusReview(@PathVariable(value = "reviewId", required = true) Integer reviewId,
                                               @PathVariable(value = "value", required = true) short value) {

        if (authenticationStatus() == "authorized"){
            UserEntity user = userService.getCurrentUser();
            userService.setReviewLikeForBook(reviewId, user, value);
            System.out.println("Лайки для отзыва = " + reviewId + " оценка = " + value);
        }
        return "redirect:/books/" + userService.getBookByReviewId(reviewId);
    }

    @PostMapping("/bookReview/{bookId}/{text}")
    public String saveNewReviewForBook(@PathVariable(value = "bookId") Integer bookId,
                                       @PathVariable(value = "text") String text) {

        if (authenticationStatus() == "authorized") {
            UserEntity user = userService.getCurrentUser();
            userService.addReviewForBook(bookService.getBookById(bookId), user, text);
            System.out.println("Оставляем отзыв для книги " + bookId + " : " + text);
        }
        return "redirect:/books/" + bookId;
    }

}
