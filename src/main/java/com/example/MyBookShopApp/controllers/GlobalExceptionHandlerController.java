package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.errs.*;
import org.springframework.beans.TypeMismatchException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.logging.Logger;

@ControllerAdvice
public class GlobalExceptionHandlerController {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private static final String TEXT = "textError";

    @ExceptionHandler(EmptySearchException.class)
    public String handleEmptySearchException(EmptySearchException ex, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute("searchError", ex.getLocalizedMessage());
        return "redirect:/";
    }
    @ExceptionHandler(NotExistBookException.class)
    public String handleNotExistBookException(NotExistBookException ex, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute(TEXT, ex.getLocalizedMessage());
        logger.info("Обработчик перехватил NotExistBookException" + ex.getMessage());
        return "redirect:/errorspage";
    }

    @ExceptionHandler(BadRequestException.class)
    public String handleBadRequestException(BadRequestException ex, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute(TEXT, ex.getLocalizedMessage());
        logger.info("Обработчик перехватил BadRequestException" + ex.getMessage());
        return "redirect:/errorspage";
    }

    @ExceptionHandler(TypeMismatchException.class)
    public String handleTypeMismatchException(NumberFormatException ex, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute(TEXT,"Введен недопустимый формат данных - "+ ex.getLocalizedMessage());
        logger.info("Обработчик перехватил TypeMismatchException" + ex.getMessage());
        return "redirect:/errorspage";
    }

        @ExceptionHandler(UsernameNotFoundException.class)
    public String handleUsernameNotFoundException(UserNotFoundException ex, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute(TEXT, ex.getLocalizedMessage());
            logger.info("Обработчик перехватил UsernameNotFoundException" + ex.getMessage());
        return "redirect:/errorspage";
    }

        @ExceptionHandler(JwtException.class)
    public String handleJwtException(JwtException ex, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute(TEXT, ex.getLocalizedMessage());
            logger.info("Обработчик перехватил JwtException");
        return "redirect:/errorspage";
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalStateException(IllegalStateException ex, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute(TEXT, ex.getLocalizedMessage());
        logger.info("Обработчик перехватил IllegalStateException");
        return "redirect:/errorspage";
    }

    @ExceptionHandler(PayException.class)
    public String handlePayException(PayException ex, RedirectAttributes redirectAttributes){
        redirectAttributes.addFlashAttribute(TEXT, ex.getLocalizedMessage());
        logger.info("Обработчик перехватил PayException");
        return "redirect:/errorspage";
    }

}
