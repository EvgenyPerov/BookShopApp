package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.responses.ResponseResultOrError;
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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

@Api("entity book")
@Controller
@RequestMapping("/books")
public class BooksController {

    private BookService bookService;
    private ResourceStorage storage;
    private UserService userService;

    private final Book2UserService book2UserService;

    private Logger logger = Logger.getLogger(this.getClass().getName());

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

    @ModelAttribute("state")
    public String authenticationState(){
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
        logger.info("Переход на страницу Новинки");
        model.addAttribute("recentResults",
                bookService.getPageOfRecentBooks(null, null, 0, 20));
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
        return new BooksPageDto(bookService.getPageOfRecentBooks(from, to, offset, limit));
    }

    // метод срабатывает выборе в главном меню
    @ApiOperation("operation to get popular books")
    @GetMapping("/popular")
    public String popularPage(Model model){
        logger.info("Переход на страницу Популярное");
        model.addAttribute("popularBooks",bookService.getPageOfPopularBooks(0, 20));
        return "/books/popular";
    }

    // метод срабатывает при карусельной подгрузке следующей партии книг на главной странице
    @ApiOperation("этот метод мапинга нужен для JS файла, постраничная загрузка популярных книг")
    @ResponseBody
    @GetMapping("/page/popular")
    public BooksPageDto getPopularBooksPage(@RequestParam("offset") Integer offset,
                                            @RequestParam("limit") Integer limit) {
        return new BooksPageDto(bookService.getPageOfPopularBooks(offset,limit));
    }


    @ApiOperation("operation to get SLUG books")
    @GetMapping("/{bookId}")
    public String getSlugOfBook(Model model,
                                @PathVariable(value = "bookId", required = false) Integer bookId) throws BadRequestException, NotExistBookException {
        logger.info("Переход на страницу SLUG book Id = " + bookId + " Тип- "+bookId.getClass());
        try {
            var book = bookService.getBookById(bookId);

            model.addAttribute("ratingMap", userService.getRatingMapByBookId(bookId));

            model.addAttribute("reviewMap", userService.getReviewMapByBookId(book));

            model.addAttribute("df", DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

            UserEntity user = userService.getCurrentUser();
            if (user != null) {
                book2UserService.addLookedBook(book, user);

                List<Book> list = new ArrayList<>();
                list.add(book);
                bookService.updateStatusOfBook(list , user);

                model.addAttribute("book", book);
                model.addAttribute("curUser", user);
                model.addAttribute("countFileDownload", storage.getCountFileDownloadByUser(book, user));
                return "/books/slugmy";
            }

            model.addAttribute("book", book);
            return "/books/slug";

        } catch (TypeMismatchException e){ //MethodArgumentTypeMismatchException NumberFormatException TypeMismatchException
            logger.info("bookController поймал ошибку NumberFormatException");
            throw new BadRequestException("Введен недопустимый формат данных");
        } catch (NoSuchElementException e){
            logger.info("bookController поймал ошибку NoSuchElementException");
            throw new NotExistBookException("Не существует книги с таким номером");
        }
    }

    @ApiOperation("operation to download books")
    @GetMapping("/download/{hash}")
    public ResponseEntity<ByteArrayResource> getSlugOfBook(@PathVariable(value = "hash", required = false) String hash) throws UnsupportedEncodingException {
        logger.info("Переход на страницу download = " + hash);

        var path = storage.getBookPathFile(hash);
        var fileName = path.getFileName().toString();
        var encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");

        var mediaType = storage.getBookFileMime(hash);

        byte[] data = storage.getBookFileByteArray(hash);

        // В описание файла добавить размер в Мбайтах
        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            storage.addFileDownload(hash, user);
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + encodedFileName)
                .contentType(mediaType)
                .contentLength(data.length)
                .body(new ByteArrayResource(data));
    }

@PostMapping("/changeBookStatus/rating/{bookId}")
@ResponseBody
    public ResponseResultOrError handleChangeBookStatusRating(@PathVariable(value = "bookId", required = true) Integer bookId,
                                                              @RequestParam(value = "value", required = false) int value) {
    var response = new ResponseResultOrError();

    if (authenticationState().equals("authorized")){
        var user = userService.getCurrentUser();
        var book = bookService.getBookById(bookId);
        boolean isSetRatingForBook = userService.setRatingForBook(book, user, value);
        if (isSetRatingForBook){
            response.setResult(true);
        }
    }
    return response;
    }


    @PostMapping("/rateBookReview/{reviewId}/{value}")
    @ResponseBody
    public ResponseResultOrError handleChangeBookStatusReview(@PathVariable(value = "reviewId") Integer reviewId,
                                                              @PathVariable(value = "value") short value) {
        logger.info("Ставим лайки для отзыва");
        var response = new ResponseResultOrError();

        if (authenticationState().equals("authorized")){
            var user = userService.getCurrentUser();
            boolean isSetReviewLikeForBook = userService.setReviewLikeForBook(reviewId, user, value);
            if (isSetReviewLikeForBook){
                response.setResult(true);
            }
        }
        return response;
    }

    @PostMapping("/bookReview/{bookId}/{text}")
    @ResponseBody
    public ResponseResultOrError saveNewReviewForBook(@PathVariable(value = "bookId") Integer bookId,
                                                      @PathVariable(value = "text") String text) {
        var response = new ResponseResultOrError();
        if (authenticationState().equals("authorized")) {
            var user = userService.getCurrentUser();
            if (user.getStatus() == 0 ) {
                response.setError("Вы заблокированы и не можете оставлять комментарии. Обратитесь в службу поддержки на странице 'Contacts'");
                return response;
            }

            if (text.length() > 15) {
                boolean isAddReviewForBook = userService.addReviewForBook(bookService.getBookById(bookId), user, text);
                if (isAddReviewForBook) {
                    response.setResult(true);
                    response.setError("OK");
                }
            } else {
                response.setError("Отзыв слишком короткий. Напишите, пожалуйста, более развёрнутый отзыв");
            }
        }
        return response;
    }

}
