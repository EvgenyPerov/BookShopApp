package com.example.MyBookShopApp.errs;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EmptySearchException extends Exception {
    public EmptySearchException(String mess) {
        super(mess);
    }
}
