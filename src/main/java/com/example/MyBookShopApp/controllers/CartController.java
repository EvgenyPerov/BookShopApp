package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.SearchWordDto;
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

@Controller
@RequestMapping("/books")
public class CartController {

    private BookService bookService;

    private Book2UserService book2UserService;

    private UserService userService;

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
                              @CookieValue(name = "cartContents", required = false) String cartContents){
        String status;
        List<Book> booksFromCookie = new ArrayList<>();
        UserEntity user = userService.getCurrentUser();

        if (user == null) {
            status = "unauthorized";
            System.out.println("страница Отложенное, берем Cookie из запроса, нет пользователя");

            if (cartContents == null || cartContents.isBlank()) {
                model.addAttribute("isCartEmpty", true);
            } else {
                model.addAttribute("isCartEmpty", false);

                booksFromCookie = bookService.getBooksFromCookies(cartContents);
            }
        } else {
            status = "authorized";
            System.out.println("страница Корзина, берем Cookie из БД для пользователя "+ user.getName());
            model.addAttribute("curUser", user);
            booksFromCookie = book2UserService.getBooksFromRepoByTypeCodeAndUser("CART",user);

            if (booksFromCookie.isEmpty()) model.addAttribute("isCartEmpty", true);
            else model.addAttribute("isCartEmpty", false);
        }

            model.addAttribute("bookCartList", booksFromCookie);

            model.addAttribute("bookCartDiscountCost", bookService.getBookCartDiscountCost(booksFromCookie));

            model.addAttribute("bookCartTotalCost", bookService.getBookCartTotalCost(booksFromCookie));

            model.addAttribute("status", status);

        return "cart";
    }

    @PostMapping("/changeBookStatus/CART/{bookIds}")
    public String handleChangeBookStatusCart(@PathVariable(value = "bookIds", required = false) String bookIdString,
                                         @CookieValue(name = "cartContents", required = false) String cartContents,
                                         @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                         HttpServletResponse response,
                                         Model model){
        UserEntity user = userService.getCurrentUser();

        String[] strArray = bookIdString.split(",");

        for (String bookId : strArray) {

            if (user == null) {

                if (cartContents == null || cartContents.isBlank()) {
                    Cookie cookieCart = new Cookie("cartContents", String.valueOf(bookId));
                    cookieCart.setPath("/");
                    cookieCart.setHttpOnly(true);
                    response.addCookie(cookieCart);
                    model.addAttribute("isCartEmpty", false);

                } else if (!cartContents.contains(String.valueOf(bookId))) {
                    StringJoiner stringJoiner = new StringJoiner("/");
                    stringJoiner.add(cartContents).add(String.valueOf(bookId));
                    Cookie cookieCart = new Cookie("cartContents", stringJoiner.toString());
                    cookieCart.setPath("/");
                    cookieCart.setHttpOnly(true);
                    response.addCookie(cookieCart);
                    model.addAttribute("isCartEmpty", false);
                }

                if (postponedContents != null && !postponedContents.isBlank()) {

                    System.out.println("Нажали Купить из отложенного");
                    ArrayList<String> cookiesKeptOld = new ArrayList<>(Arrays.asList(postponedContents.split("/")));
                    cookiesKeptOld.remove(String.valueOf(bookId));
                    Cookie cookiesKeptNew = new Cookie("postponedContents", String.join("/", cookiesKeptOld));
                    cookiesKeptNew.setPath("/");
                    cookiesKeptNew.setHttpOnly(true);
                    response.addCookie(cookiesKeptNew);

                }

            } else {
                bookService.increaseCart(Integer.valueOf(bookId));
                bookService.decreaseKept(Integer.valueOf(bookId));
                book2UserService.update("CART", bookService.getBookById(Integer.valueOf(bookId)), user);
            }
        }
        return "redirect:/books/postponed";
    }

    @PostMapping("/changeBookStatus/UNLINK/CART/{bookId}")
    public String handleChangeBookStatusCartRemove(@PathVariable(value = "bookId", required = false) Integer bookId,
                                   @CookieValue(name = "cartContents", required = false) String cartContents,
                                   HttpServletResponse response,
                                   Model model){
        System.out.println("Удаляем книгу из Корзины");
        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            bookService.decreaseCart(bookId);
            book2UserService.delete(bookService.getBookById(bookId), user);
        } else {

            if (cartContents != null && !cartContents.isBlank()) {
                ArrayList<String> cookies = new ArrayList<>(Arrays.asList(cartContents.split("/")));
                System.out.println("было кук = " + cookies.size());
                cookies.remove(String.valueOf(bookId));
                System.out.println("стало кук = " + cookies.size());
                Cookie cookie = new Cookie("cartContents", String.join("/", cookies));

                if (!cookies.isEmpty()) {
                    model.addAttribute("isCartEmpty", false);
                } else {
                    model.addAttribute("isCartEmpty", true);
                }

                cookie.setPath("/");
                cookie.setHttpOnly(true);
                response.addCookie(cookie);
            } else {
                model.addAttribute("isCartEmpty", true);
            }
        }

        return "redirect:/books/cart";
    }
}
