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
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@Controller
@RequestMapping("/books")
public class PostponedController {

    private BookService bookService;

    private Book2UserService book2UserService;

    private UserService userService;

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

    @ModelAttribute("bookCartList")
    public List<Book> geBookCartList(){
        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            return book2UserService.getCookieBooksFromRepoByTypeCode("CART",user);
        } else return new ArrayList<>();
    }

    @GetMapping("/postponed")
    public String postponedPage(Model model,
                                @CookieValue(name = "postponedContents", required = false) String postponedContents){
        String status;
        List<Book> booksFromCookie = null;
        UserEntity user = userService.getCurrentUser();

        if (user == null) {
            status = "unauthorized";
            System.out.println("страница Отложенное, берем Cookie из запроса, нет пользователя");

            if (postponedContents == null || postponedContents.isBlank()) {
                model.addAttribute("isPostponedEmpty", true);
            } else {
                model.addAttribute("isPostponedEmpty", false);

                postponedContents = postponedContents.startsWith("/") ?
                        postponedContents.substring(1) : postponedContents;

                postponedContents = postponedContents.endsWith("/") ?
                        postponedContents.substring(0, postponedContents.length() - 1) : postponedContents;

                String[] arrayIds = postponedContents.split("/");

                booksFromCookie = bookService.getBooksByIdIn(arrayIds);
            }

        } else {
            status = "authorized";
            System.out.println("страница Отложенное, берем Cookie из БД для пользователя "+ user.getName());
            model.addAttribute("curUser", user);
            booksFromCookie = book2UserService.getCookieBooksFromRepoByTypeCode("KEPT",user);

            if (booksFromCookie.isEmpty()) model.addAttribute("isPostponedEmpty", true);
            else model.addAttribute("isPostponedEmpty", false);
        }

        model.addAttribute("bookPostponedList", booksFromCookie);

        model.addAttribute("idListPostponedBooks", bookService.getIdListPostponedBooks(booksFromCookie));

        model.addAttribute("status", status);

        return "postponed";
    }

    @PostMapping("/changeBookStatus/KEPT/{bookId}")
    public String handleChangeBookStatus(@PathVariable(value = "bookId", required = false) Integer bookId,
                                         @CookieValue(name = "cartContents", required = false) String cartContents,
                                         @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                         HttpServletResponse response,
                                         Model model){

        UserEntity user = userService.getCurrentUser();

        if (user == null) {

            if (postponedContents == null || postponedContents.isBlank()) {
                Cookie cookie = new Cookie("postponedContents", String.valueOf(bookId));
                cookie.setPath("/books");
                response.addCookie(cookie);
                model.addAttribute("isPostponedEmpty", false);
            } else if (!postponedContents.contains(String.valueOf(bookId))) {
                StringJoiner stringJoiner = new StringJoiner("/");
                stringJoiner.add(postponedContents).add(String.valueOf(bookId));
                Cookie cookie = new Cookie("postponedContents", stringJoiner.toString());
                cookie.setPath("/books");
                response.addCookie(cookie);
                model.addAttribute("isPostponedEmpty", false);
            }

            if (cartContents != null && !cartContents.isBlank()) {
                System.out.println("Нажали Отложить из корзины");

                ArrayList<String> cookiesCartOld = new ArrayList<>(Arrays.asList(cartContents.split("/")));
                cookiesCartOld.remove(String.valueOf(bookId));
                Cookie cookiesCartNew = new Cookie("cartContents", String.join("/", cookiesCartOld));
                cookiesCartNew.setPath("/books");
                cookiesCartNew.setHttpOnly(true);
                response.addCookie(cookiesCartNew);
            }
        } else {
            bookService.increaseKept(bookId);
            bookService.decreaseCart(bookId);
            book2UserService.update("KEPT", bookService.getBookById(bookId), user);
        }
        return "redirect:/books/" + bookId;
    }

    @PostMapping("/changeBookStatus/UNLINK/KEPT/{bookId}")
    public String handleCartRemove(@PathVariable(value = "bookId", required = false) Integer bookId,
                                   @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                   HttpServletResponse response,
                                   Model model){
        System.out.println("Удаляем книгу из отложенного");
        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            bookService.decreaseKept(bookId);
            book2UserService.delete(bookService.getBookById(bookId), user);
        } else {

            if (postponedContents != null && !postponedContents.isBlank()) {
                ArrayList<String> cookies = new ArrayList<>(Arrays.asList(postponedContents.split("/")));
                System.out.println("было кук = " + cookies.size() + " " + cookies.get(0));
                cookies.remove(String.valueOf(bookId));
                System.out.println("стало кук = " + cookies.size());
                Cookie cookie = new Cookie("postponedContents", String.join("/", cookies));
                cookie.setPath("/books");
                cookie.setHttpOnly(true);
                response.addCookie(cookie);
                model.addAttribute("isPostponedEmpty", false);
            } else {
                model.addAttribute("isPostponedEmpty", true);
            }
        }

        return "redirect:/books/postponed";
    }

}
