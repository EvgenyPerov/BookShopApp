package com.example.MyBookShopApp.security;

import lombok.Data;

@Data
public class RegistrationForm {

    String name;

    String email;

    private String phone;

    private String pass;

    private String pass2;
}
