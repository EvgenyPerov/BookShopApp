package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.data.responses.ResponseResultOrError;
import com.example.MyBookShopApp.data.services.Book2UserService;
import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.data.services.UserService;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Logger;

@Controller
@RequestMapping("/books")
public class PostponedController {

    private BookService bookService;

    private Book2UserService book2UserService;

    private UserService userService;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private static final String IS_POSTPONED_EMPTY = "isPostponedEmpty";

    @Autowired
    public PostponedController(BookService bookService, Book2UserService book2UserService, UserService userService) {
        this.bookService = bookService;
        this.book2UserService = book2UserService;
        this.userService = userService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto(){
        return new SearchWordDto();
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

    @ModelAttribute("bookCartList")
    public List<Book> geBookCartList(@CookieValue(name = "cartContents", required = false) String cartContents){
        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            return book2UserService.getBooksFromRepoByTypeCodeAndUser("CART",user);
        } else {
            return bookService.getBooksFromCookies(cartContents);
        }
    }

    @GetMapping("/postponed")
    public String postponedPage(Model model,
                                @CookieValue(name = "postponedContents", required = false) String postponedContents){
        String state;
        List<Book> books = new ArrayList<>();
        UserEntity user = userService.getCurrentUser();

        if (user == null) {
            state = "unauthorized";
            logger.info("страница Отложенное, берем Cookie из запроса, нет пользователя");

            if (postponedContents == null || postponedContents.isBlank()) {
                model.addAttribute(IS_POSTPONED_EMPTY, true);
            } else {
                model.addAttribute(IS_POSTPONED_EMPTY, false);

                books = bookService.getBooksFromCookies(postponedContents);
            }

        } else {
            state = "authorized";
            logger.info("страница Отложенное, берем Cookie из БД для пользователя "+ user.getName());
            model.addAttribute("curUser", user);
            books = book2UserService.getBooksFromRepoByTypeCodeAndUser("KEPT",user);

            if (books.isEmpty()) {
                model.addAttribute(IS_POSTPONED_EMPTY, true);
            }
            else {
                model.addAttribute(IS_POSTPONED_EMPTY, false);
                book2UserService.updateStatusOfBook(books, user);
            }
        }

        model.addAttribute("bookPostponedList", books);

        model.addAttribute("idListPostponedBooks", bookService.getIdListPostponedBooks(books));

        model.addAttribute("state", state);

        return "postponed";
    }

    @PostMapping("/changeBookStatus/KEPT/{bookId}/{source}")
    @ResponseBody
    public ResponseResultOrError handleChangeBookStatus(@PathVariable(value = "bookId", required = false) Integer bookId,
                                         @PathVariable(value = "source", required = false) String source,
                                         @CookieValue(name = "cartContents", required = false) String cartContents,
                                         @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                         HttpServletResponse httpServletResponse,
                                         Model model){
        var response = new ResponseResultOrError();
        response.setError("Error");
        response.setResult(true);

        UserEntity user = userService.getCurrentUser();

        if (user == null) {

            if (postponedContents == null || postponedContents.isBlank()) {
                var cookie = new Cookie("postponedContents", String.valueOf(bookId));
                cookie.setPath("/");
                httpServletResponse.addCookie(cookie);
                model.addAttribute(IS_POSTPONED_EMPTY, false);
            } else if (!postponedContents.contains(String.valueOf(bookId))) {
                var stringJoiner = new StringJoiner("/");
                stringJoiner.add(postponedContents).add(String.valueOf(bookId));
                var cookie = new Cookie("postponedContents", stringJoiner.toString());
                cookie.setPath("/");
                httpServletResponse.addCookie(cookie);
                model.addAttribute(IS_POSTPONED_EMPTY, false);
            }

            if (cartContents != null && !cartContents.isBlank()) {

                ArrayList<String> cookiesCartOld = new ArrayList<>(Arrays.asList(cartContents.split("/")));
                cookiesCartOld.remove(String.valueOf(bookId));
                var cookiesCartNew = new Cookie("cartContents", String.join("/", cookiesCartOld));
                cookiesCartNew.setPath("/");
                cookiesCartNew.setHttpOnly(true);
                httpServletResponse.addCookie(cookiesCartNew);
            }
        } else {

            if (source.equals("cart")){
                bookService.decreaseCart(bookId);
            }

            if (book2UserService.update("KEPT", bookService.getBookById(bookId), user)) {
                bookService.increaseKept(bookId);
            }

        }
        return response;
    }

    @PostMapping("/changeBookStatus/UNLINK/KEPT/{bookId}/{source}")
    @ResponseBody
    public ResponseResultOrError handleCartRemove(@PathVariable(value = "bookId", required = false) Integer bookId,
                                   @PathVariable(value = "source", required = false) String source,
                                   @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                   HttpServletResponse httpServletResponse,
                                   Model model){
        logger.info("Удаляем книгу из отложенного");
        var response = new ResponseResultOrError();
        response.setError("Error");
        response.setResult(true);
        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            bookService.decreaseKept(bookId);
            book2UserService.delete(bookService.getBookById(bookId), user);

        } else {

            if (postponedContents != null && !postponedContents.isBlank()) {
                ArrayList<String> cookies = new ArrayList<>(Arrays.asList(postponedContents.split("/")));
                cookies.remove(String.valueOf(bookId));
                var cookie = new Cookie("postponedContents", String.join("/", cookies));
                if (!cookies.isEmpty()) {
                    model.addAttribute(IS_POSTPONED_EMPTY, false);
                } else {
                    model.addAttribute(IS_POSTPONED_EMPTY, true);
                }
                    cookie.setPath("/");
                    cookie.setHttpOnly(true);
                httpServletResponse.addCookie(cookie);
                } else {
                model.addAttribute(IS_POSTPONED_EMPTY, true);
            }
        }

        return response;
    }

}
