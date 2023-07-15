package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.data.services.Book2UserService;
import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.data.services.UserService;
import com.example.MyBookShopApp.security.jwt.JwtService;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class AuthUserController {

    private UserService userService;

    private final BookService bookService;

    private final JwtService jwtService;

    private final Book2UserService book2UserService;

    @Autowired
    public AuthUserController(UserService userService, BookService bookService, JwtService jwtService, Book2UserService book2UserService) {
        this.userService = userService;
        this.bookService = bookService;
        this.jwtService = jwtService;
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

    @GetMapping("/signin")
    public String signInPage(){
        System.out.println("Переход на страницу Вход");
//        System.out.println("Авторизован - " + SecurityContextHolder.getContext().getAuthentication() != null);
        return "signin";
    }

    @GetMapping("/signup")
    public String signUpPage(Model model){
        System.out.println("Переход на страницу Регистрация");
        model.addAttribute("regForm", new RegistrationForm());
        return "signup";
    }

    @PostMapping("/requestContactConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestContactConfirmation(@RequestBody ContactConfirmationPayload payload){
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");
        return response;
    }

    @PostMapping("/approveContact")
    @ResponseBody
    public ContactConfirmationResponse handleApproveContact(@RequestBody ContactConfirmationPayload payload){
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");
        return response;
    }

    @PostMapping("/reg")
    public String signUpRegistrationPage(RegistrationForm form, Model model){
        userService.addUser(form);
        model.addAttribute("regOk", true);
        System.out.println("Пользователь добавлен в БД, регистрация прошла успешно");
        return "redirect:/signin";
    }

    @PostMapping("/login")
    @ResponseBody
    public ContactConfirmationResponse handleLogin(@RequestBody ContactConfirmationPayload payload,
                                                   HttpServletResponse httpServletResponse){
        ContactConfirmationResponse loginResponse = userService.jwtLogin(payload);

        if (jwtService.isTokenInBlacklist(loginResponse.getResult())){
            System.out.println("Токен недействителен");
            return null;
        }

        Cookie cookie = new Cookie("token", loginResponse.getResult());
        httpServletResponse.addCookie(cookie);

        return loginResponse;
    }

    @GetMapping("/my")
    public String myPage(Model model,
                         @CookieValue(name = "cartContents", required = false) String cartContents,
                         @CookieValue(name = "postponedContents", required = false) String postponedContents){
        System.out.println("Переход на страницу My");


        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            model.addAttribute("curUser", user);
            model.addAttribute("myPaidBooks", book2UserService.getBooksFromRepoByTypeCodeAndUser("PAID", user));

            List<Book> booksPostponed = bookService.getBooksFromCookies(postponedContents);
                if (booksPostponed != null) {
                    for (Book book : booksPostponed) {
                        book2UserService.update("KEPT", book, user);
                    }
                }

            List<Book> booksCart = bookService.getBooksFromCookies(cartContents);
                if (booksCart != null) {
                    for (Book book : booksCart) {
                        book2UserService.update("CART", book, user);
                    }
                }
        }

        return "my";
    }

    @GetMapping("/my/archive")
    public String myPage(Model model) {
        System.out.println("Переход на страницу MyArchive");

        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            model.addAttribute("curUser", user);
            model.addAttribute("myArchihedBooks", book2UserService.getBooksFromRepoByTypeCodeAndUser("ARCHIVED", user));
        }

        return "myarchive";
    }


    @GetMapping("/profile")
    public String profilePage(Model model){
        System.out.println("Переход на страницу Profile");
        model.addAttribute("curUser", userService.getCurrentUser());
        return "profile";
    }

//    @GetMapping("/logoutMy")
//    public String logout(HttpServletRequest request){
//        System.out.println("сработал контроллер и GetMapping - logout");
//
//        Cookie[] cookies = request.getCookies();
//
//        if (cookies != null){
//            for (Cookie cookie : cookies){
//                System.out.println("При /logout для cookie- " + cookie.getName()
//                        + " время действия токена = " + cookie.getMaxAge());
//            }
//        }
//        HttpSession session = request.getSession();
//
//        SecurityContextHolder.clearContext();
//
//        if (session != null){ session.invalidate();}
//
//        return "redirect:/signin";
//    }
}
