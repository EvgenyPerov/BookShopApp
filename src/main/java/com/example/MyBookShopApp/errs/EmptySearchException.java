package com.example.MyBookShopApp.errs;

public class EmptySearchException extends Exception {
    public EmptySearchException(String mess) {
        super(mess);
    }
}
