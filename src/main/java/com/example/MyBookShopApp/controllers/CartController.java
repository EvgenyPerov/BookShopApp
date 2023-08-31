package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.data.services.Book2UserService;
import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.data.services.PaymentServise;
import com.example.MyBookShopApp.data.services.UserService;
import com.example.MyBookShopApp.errs.PayException;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
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

    private final PaymentServise paymentServise;

    @Autowired
    public CartController(BookService bookService, Book2UserService book2UserService, UserService userService, PaymentServise paymentServise) {
        this.bookService = bookService;
        this.book2UserService = book2UserService;
        this.userService = userService;
        this.paymentServise = paymentServise;
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

        String status;
        List<Book> books = new ArrayList<>();
        UserEntity user = userService.getCurrentUser();

        if (user == null) {
            status = "unauthorized";
            System.out.println("страница Отложенное, берем Cookie из запроса, нет пользователя");

            if (cartContents == null || cartContents.isBlank()) {
                model.addAttribute("isCartEmpty", true);
            } else {
                model.addAttribute("isCartEmpty", false);

                books = bookService.getBooksFromCookies(cartContents);
            }
        } else {
            status = "authorized";
            System.out.println("страница Корзина, берем Cookie из БД для пользователя "+ user.getName());
            model.addAttribute("curUser", user);
            books = book2UserService.getBooksFromRepoByTypeCodeAndUser("CART",user);

            if (books.isEmpty()) {
                model.addAttribute("isCartEmpty", true);
            } else {
                model.addAttribute("isCartEmpty", false);
                model.addAttribute("balance", user.getBalance());
                System.out.println("Баланс=  "+ user.getBalance());
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

            model.addAttribute("status", status);

            model.addAttribute("PaymentOk", isPaymentOk);

            model.addAttribute("errorMessage", errorMessage);

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
