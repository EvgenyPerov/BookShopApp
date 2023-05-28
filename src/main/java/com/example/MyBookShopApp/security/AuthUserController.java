package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.data.services.UserService;
import com.example.MyBookShopApp.security.jwt.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
public class AuthUserController {

    private UserService userService;

    private final JwtService jwtService;

    @Autowired
    public AuthUserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto(){
        return new SearchWordDto();
    }

    @ModelAttribute("status")
    public String authenticationStatus(){
        return (userService.getCurrentUser() == null)? "unauthorized" : "authorized";
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
    public String myPage(Model model){
        System.out.println("Переход на страницу My");
        model.addAttribute("curUser", userService.getCurrentUser());
        return "my";
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
