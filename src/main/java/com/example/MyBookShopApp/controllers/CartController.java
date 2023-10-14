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
public class CartController {

    private BookService bookService;

    private Book2UserService book2UserService;

    private UserService userService;

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private static final String IS_CART_EMPTY = "isCartEmpty";

    @Autowired
    public CartController(BookService bookService, Book2UserService book2UserService, UserService userService) {
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

    @ModelAttribute("bookPostponedList")
    public List<Book> geBookPostponedList(@CookieValue(name = "postponedContents", required = false) String postponedContents){
        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            return book2UserService.getBooksFromRepoByTypeCodeAndUser("KEPT", user);
        } else {
            return bookService.getBooksFromCookies(postponedContents);
        }
    }

    @GetMapping({"/cart"})
    public String getCartPage(Model model,
                              @CookieValue(name = "cartContents", required = false) String cartContents,
                              @RequestParam(value = "result", required = false) Boolean isPaymentOk,
                              @RequestParam(value = "error", required = false) String errorMessage) {

        String state;
        List<Book> books = new ArrayList<>();
        UserEntity user = userService.getCurrentUser();

        if (user == null) {
            state = "unauthorized";
            logger.info("страница Отложенное, берем Cookie из запроса, нет пользователя");

            if (cartContents == null || cartContents.isBlank()) {
                model.addAttribute(IS_CART_EMPTY, true);
            } else {
                model.addAttribute(IS_CART_EMPTY, false);

                books = bookService.getBooksFromCookies(cartContents);
            }
        } else {
            state = "authorized";
            logger.info("страница Корзина, берем Cookie из БД для пользователя "+ user.getName());
            model.addAttribute("curUser", user);
            books = book2UserService.getBooksFromRepoByTypeCodeAndUser("CART",user);

            if (books.isEmpty()) {
                model.addAttribute(IS_CART_EMPTY, true);
            } else {
                book2UserService.updateStatusOfBook(books, user);
                model.addAttribute(IS_CART_EMPTY, false);
                model.addAttribute("balance", user.getBalance());
                if (isPaymentOk != null && isPaymentOk){
                    for (Book book : books) {
                        book2UserService.update("PAID", book, user);
                    }
                    return "redirect:/books/cart";
                }
            }
        }

            model.addAttribute("bookCartList", books);

            model.addAttribute("bookCartDiscountCost", bookService.getBookCartDiscountCost(books));

            model.addAttribute("bookCartTotalCost", bookService.getBookCartTotalCost(books));

            model.addAttribute("state", state);

            model.addAttribute("PaymentOk", isPaymentOk);

            model.addAttribute("errorMessage", errorMessage);

        return "cart";
    }

    @PostMapping("/changeBookStatus/CART/{bookIds}/{source}")
    @ResponseBody
    public ResponseResultOrError handleChangeBookStatusCart(@PathVariable(value = "bookIds", required = false) String bookIdString,
                                             @PathVariable(value = "source", required = false) String source,
                                         @CookieValue(name = "cartContents", required = false) String cartContents,
                                         @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                         HttpServletResponse httpServletResponse,
                                         Model model){
        var response = new ResponseResultOrError();
        response.setError("Error");
        response.setResult(true);
        UserEntity user = userService.getCurrentUser();

        String[] strArray = bookIdString.split(",");

        for (String bookId : strArray) {

            if (user == null) {

                if (cartContents == null || cartContents.isBlank()) {
                    var cookieCart = new Cookie("cartContents", String.valueOf(bookId));
                    cookieCart.setPath("/");
                    cookieCart.setHttpOnly(true);
                    httpServletResponse.addCookie(cookieCart);
                    model.addAttribute(IS_CART_EMPTY, false);

                } else if (!cartContents.contains(String.valueOf(bookId))) {
                    var stringJoiner = new StringJoiner("/");
                    stringJoiner.add(cartContents).add(String.valueOf(bookId));
                    var cookieCart = new Cookie("cartContents", stringJoiner.toString());
                    cookieCart.setPath("/");
                    cookieCart.setHttpOnly(true);
                    httpServletResponse.addCookie(cookieCart);
                    model.addAttribute(IS_CART_EMPTY, false);
                }

                if (postponedContents != null && !postponedContents.isBlank()) {

                    ArrayList<String> cookiesKeptOld = new ArrayList<>(Arrays.asList(postponedContents.split("/")));
                    cookiesKeptOld.remove(String.valueOf(bookId));
                    var cookiesKeptNew = new Cookie("postponedContents", String.join("/", cookiesKeptOld));
                    cookiesKeptNew.setPath("/");
                    cookiesKeptNew.setHttpOnly(true);
                    httpServletResponse.addCookie(cookiesKeptNew);
                }
            } else {
                if (source.equals("postponed")){
                    bookService.decreaseKept(Integer.valueOf(bookId));
                }

                if (book2UserService.update("CART", bookService.getBookById(Integer.valueOf(bookId)), user)) {
                    bookService.increaseCart(Integer.valueOf(bookId));
                }

            }
        }
        return response;
    }

    @PostMapping("/changeBookStatus/UNLINK/CART/{bookId}/{source}")
    @ResponseBody
    public ResponseResultOrError handleChangeBookStatusCartRemove(@PathVariable(value = "bookId", required = false) Integer bookId,
                                                   @PathVariable(value = "source", required = false) String source,
                                   @CookieValue(name = "cartContents", required = false) String cartContents,
                                   HttpServletResponse httpServletResponse,
                                   Model model){
        logger.info("Удаляем книгу из Корзины");
        var response = new ResponseResultOrError();
        response.setError("Error");
        response.setResult(true);

        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            bookService.decreaseCart(bookId);
            book2UserService.delete(bookService.getBookById(bookId), user);
        } else {

            if (cartContents != null && !cartContents.isBlank()) {
                ArrayList<String> cookies = new ArrayList<>(Arrays.asList(cartContents.split("/")));
                cookies.remove(String.valueOf(bookId));
                var cookie = new Cookie("cartContents", String.join("/", cookies));

                if (!cookies.isEmpty()) {
                    model.addAttribute(IS_CART_EMPTY, false);
                } else {
                    model.addAttribute(IS_CART_EMPTY, true);
                }

                cookie.setPath("/");
                cookie.setHttpOnly(true);
                httpServletResponse.addCookie(cookie);
            } else {
                model.addAttribute(IS_CART_EMPTY, true);
            }
        }

        return response;
    }

   }
