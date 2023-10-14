package com.example.MyBookShopApp.security;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class RegistrationForm {

    private int id;

    @Size(min=2, message = "Не меньше 2 знаков")
    private String name;

    private String email;

    private String phone;

    @Size(min=5, message = "Не меньше 5 знаков")
    private String pass;

    private String pass2;
}
