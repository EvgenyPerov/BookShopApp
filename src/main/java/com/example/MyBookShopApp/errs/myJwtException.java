package com.example.MyBookShopApp.errs;

import org.springframework.security.oauth2.jwt.JwtException;

//@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class myJwtException extends JwtException {
    public myJwtException(String mess) {
        super(mess);
    }

}
