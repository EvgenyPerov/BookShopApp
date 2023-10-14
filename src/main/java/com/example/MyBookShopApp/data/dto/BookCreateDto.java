package com.example.MyBookShopApp.data.dto;

import lombok.Data;

@Data
public class BookCreateDto {

    private int id;

    private String pubDate;

    private Boolean isBestseller;

    private String slug;

    private String title;

    private String image;

    private String description;

    private int price;

    private int discount;

    private String author;

    private String genre;

    private String tag;

    private String file;

}
