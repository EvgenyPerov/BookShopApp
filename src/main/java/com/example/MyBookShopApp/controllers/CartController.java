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
import java.time.LocalDateTime;
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

    @ModelAttribute("bookCartList")
    public List<Book> bookCart(){
        return new ArrayList<>();
    }

    @GetMapping({"/cart"})
    public String getCartPage(Model model,
                              @CookieValue(name = "cartContents", required = false) String cartContents){
        System.out.println("Переход на страницу Корзина");
        if (cartContents == null || cartContents.isBlank()){
            model.addAttribute("isCartEmpty", true);
        } else {
            model.addAttribute("isCartEmpty", false);
            cartContents = cartContents.startsWith("/")? cartContents.substring(1) : cartContents;
            cartContents = cartContents.endsWith("/")? cartContents.substring(0,cartContents.length()-1) : cartContents;

            String[] arrayIds = cartContents.split("/");

            List<Book> booksFromCookie = new ArrayList<>();
            booksFromCookie = bookService.getBooksByIdIn(arrayIds);

            model.addAttribute("bookCartList", booksFromCookie);
        }
        return "cart";
    }

    @PostMapping("/changeBookStatus/CART/{bookId}")
    public String handleChangeBookStatusCart(@PathVariable(value = "bookId", required = false) Integer bookId,
                                         @CookieValue(name = "cartContents", required = false) String cartContents,
                                         @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                         HttpServletResponse response,
                                         Model model){
        String hashUser = "hash44444";
        UserEntity user = userService.getUserByHash(hashUser);

        System.out.println("Сработал мапинг /changeBookStatus/CART/{bookId}");

        if (cartContents == null || cartContents.isBlank()){
            Cookie cookieCart = new Cookie("cartContents", String.valueOf(bookId));
            cookieCart.setPath("/books");
            cookieCart.setHttpOnly(true);
            response.addCookie(cookieCart);
            model.addAttribute("isCartEmpty", false);

            bookService.increaseCart(bookId);

            book2UserService.update("CART",bookService.getBookById(bookId), user);

        } else
            if (!cartContents.contains(String.valueOf(bookId))) {
                StringJoiner stringJoiner = new StringJoiner("/");
                stringJoiner.add(cartContents).add(String.valueOf(bookId));
                Cookie cookieCart = new Cookie("cartContents", stringJoiner.toString());
                cookieCart.setPath("/books");
                cookieCart.setHttpOnly(true);
                response.addCookie(cookieCart);
                model.addAttribute("isCartEmpty", false);

                bookService.increaseCart(bookId);

                book2UserService.update("CART",bookService.getBookById(bookId), user);
        }

        if (postponedContents != null && !postponedContents.isBlank()){

            System.out.println("Нажали Купить из отложенного");
            ArrayList<String> cookiesKeptOld = new ArrayList<>(Arrays.asList(postponedContents.split("/")));
            cookiesKeptOld.remove(String.valueOf(bookId));
            Cookie cookiesKeptNew = new Cookie("postponedContents", String.join("/", cookiesKeptOld));
            cookiesKeptNew.setPath("/books");
            cookiesKeptNew.setHttpOnly(true);
            response.addCookie(cookiesKeptNew);

            bookService.decreaseKept(bookId);

        }

        return "redirect:/books/" + bookId;

    }


    @PostMapping("/changeBookStatus/UNLINK/CART/{bookId}")
    public String handleChangeBookStatusCartRemove(@PathVariable(value = "bookId", required = false) Integer bookId,
                                   @CookieValue(name = "cartContents", required = false) String cartContents,
                                   HttpServletResponse response,
                                   Model model){
        String hashUser = "hash44444";
        UserEntity user = userService.getUserByHash(hashUser);

        if (cartContents != null && !cartContents.isBlank()){
            ArrayList<String> cookies = new ArrayList<>(Arrays.asList(cartContents.split("/")));
            System.out.println("было кук = " + cookies.size() + " " + cookies.get(0));
            cookies.remove(String.valueOf(bookId));
            System.out.println("стало кук = " + cookies.size());
            Cookie cookie = new Cookie("cartContents", String.join("/", cookies));
            cookie.setPath("/books");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            model.addAttribute("isCartEmpty", false);

            bookService.decreaseCart(bookId);

        } else {
            model.addAttribute("isCartEmpty", true);

            bookService.decreaseCart(bookId);
        }

        book2UserService.delete(bookService.getBookById(bookId), user);

        return "redirect:/books/cart";
    }
}
