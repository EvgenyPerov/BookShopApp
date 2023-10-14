package com.example.MyBookShopApp.data.dto;

import lombok.Data;

@Data
public class AuthorCreateDto {

    private int id;

    private String photo;

    private String slug;

    private String name;

    private String description;

}
