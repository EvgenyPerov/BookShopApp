package com.example.MyBookShopApp.errs;

public class BadRequestException extends Exception {
    public BadRequestException(String mess) {
        super(mess);
    }

}
