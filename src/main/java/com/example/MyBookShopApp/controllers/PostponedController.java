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

    @ModelAttribute("bookPostponedList")
    public List<Book> bookPostponed(){
        return new ArrayList<>();
    }

    @GetMapping("/postponed")
    public String postponedPage(Model model,
                                @CookieValue(name = "postponedContents", required = false) String postponedContents){
        System.out.println("Переход на страницу Отложенное");
        if (postponedContents == null || postponedContents.isBlank()){
            model.addAttribute("isPostponedEmpty", true);
        } else {
            model.addAttribute("isPostponedEmpty", false);

            postponedContents = postponedContents.startsWith("/")?
                    postponedContents.substring(1) : postponedContents;

            postponedContents = postponedContents.endsWith("/")?
                    postponedContents.substring(0,postponedContents.length()-1) : postponedContents;

            String[] arrayIds = postponedContents.split("/");

            List<Book> booksFromCookie = new ArrayList<>();
            booksFromCookie = bookService.getBooksByIdIn(arrayIds);

            model.addAttribute("bookPostponedList", booksFromCookie);
        }
        return "postponed";
    }

    @PostMapping("/changeBookStatus/KEPT/{bookId}")
    public String handleChangeBookStatus(@PathVariable(value = "bookId", required = false) Integer bookId,
                                         @CookieValue(name = "cartContents", required = false) String cartContents,
                                         @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                         HttpServletResponse response,
                                         Model model){
        String hashUser = "hash44444";
        UserEntity user = userService.getUserByHash(hashUser);

        System.out.println("Сработал мапинг /changeBookStatus/KEPT/{bookId}");

        if (postponedContents == null || postponedContents.isBlank()){
            Cookie cookie = new Cookie("postponedContents", String.valueOf(bookId));
            cookie.setPath("/books");
            response.addCookie(cookie);
            model.addAttribute("isPostponedEmpty", false);

            bookService.increaseKept(bookId);

            book2UserService.update("KEPT",bookService.getBookById(bookId), user);

        } else
        if (!postponedContents.contains(String.valueOf(bookId))) {
            StringJoiner stringJoiner = new StringJoiner("/");
            stringJoiner.add(postponedContents).add(String.valueOf(bookId));
            Cookie cookie = new Cookie("postponedContents", stringJoiner.toString());
            cookie.setPath("/books");
            response.addCookie(cookie);
            model.addAttribute("isPostponedEmpty", false);

            bookService.increaseKept(bookId);

            book2UserService.update("KEPT",bookService.getBookById(bookId), user);
        }

        if (cartContents != null && !cartContents.isBlank()){
            System.out.println("Нажали Отложить из корзины");

            ArrayList<String> cookiesCartOld = new ArrayList<>(Arrays.asList(cartContents.split("/")));
            cookiesCartOld.remove(String.valueOf(bookId));
            Cookie cookiesCartNew = new Cookie("cartContents", String.join("/", cookiesCartOld));
            cookiesCartNew.setPath("/books");
            cookiesCartNew.setHttpOnly(true);
            response.addCookie(cookiesCartNew);

            bookService.decreaseCart(bookId);

        }
        return "redirect:/books/" + bookId;
    }

    @PostMapping("/changeBookStatus/UNLINK/KEPT/{bookId}")
    public String handleCartRemove(@PathVariable(value = "bookId", required = false) Integer bookId,
                                   @CookieValue(name = "postponedContents", required = false) String postponedContents,
                                   HttpServletResponse response,
                                   Model model){
        String hashUser = "hash44444";
        UserEntity user = userService.getUserByHash(hashUser);

        if (postponedContents != null && !postponedContents.isBlank()){
            ArrayList<String> cookies = new ArrayList<>(Arrays.asList(postponedContents.split("/")));
            System.out.println("было кук = " + cookies.size() + " " + cookies.get(0));
            cookies.remove(String.valueOf(bookId));
            System.out.println("стало кук = " + cookies.size());
            Cookie cookie = new Cookie("postponedContents", String.join("/", cookies));
            cookie.setPath("/books");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            model.addAttribute("isPostponedEmpty", false);

            bookService.decreaseKept(bookId);
        } else {
            model.addAttribute("isPostponedEmpty", true);

            bookService.decreaseKept(bookId);
        }

        book2UserService.delete(bookService.getBookById(bookId), user);

        return "redirect:/books/postponed";
    }

}
