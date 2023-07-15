package com.example.MyBookShopApp.errs;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR) //500
public class UserNotFoundException extends Exception {
    public UserNotFoundException(String mess) {
        super(mess);
    }
}
