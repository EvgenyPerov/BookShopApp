package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.services.UserService;
import com.example.MyBookShopApp.errs.*;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandlerController {

    @ExceptionHandler(EmptySearchException.class)
    public String handleEmptySearchException(EmptySearchException ex, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("searchError", ex.getLocalizedMessage());
        return "redirect:/";
    }
    @ExceptionHandler(NotExistBookException.class)
    public String handleNotExistBookException(NotExistBookException ex, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("textError", ex.getLocalizedMessage());
        System.out.println("Обработчик перехватил NotExistBookException" + ex.getMessage());
        return "redirect:/errorspage";
    }

    @ExceptionHandler(BadRequestException.class)
    public String handleBadRequestException(BadRequestException ex, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("textError", ex.getLocalizedMessage());
        System.out.println("Обработчик перехватил BadRequestException" + ex.getMessage());
        return "redirect:/errorspage";
    }

    @ExceptionHandler(TypeMismatchException.class)
    public String handleTypeMismatchException(NumberFormatException ex, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("textError","Введен недопустимый формат данных - "+ ex.getLocalizedMessage());
        //NumberFormatException MethodArgumentTypeMismatchException TypeMismatchException
        System.out.println("Обработчик перехватил TypeMismatchException" + ex.getMessage());
        return "redirect:/errorspage";
    }

        @ExceptionHandler(UsernameNotFoundException.class)
    public String handleUsernameNotFoundException(UserNotFoundException ex, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("textError", ex.getLocalizedMessage());
        System.out.println("Обработчик перехватил UsernameNotFoundException" + ex.getMessage());
        return "redirect:/errorspage";
    }

        @ExceptionHandler(JwtException.class)
    public String handleJwtException(JwtException ex, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("textError", ex.getLocalizedMessage());
        System.out.println("Обработчик перехватил JwtException");
        return "redirect:/errorspage";
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException ex, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("textError", ex.getLocalizedMessage());
        System.out.println("Обработчик перехватил IllegalStateException");
        return "redirect:/errorspage";
    }

}
