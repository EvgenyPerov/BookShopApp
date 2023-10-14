package com.example.MyBookShopApp.data.dto;

import lombok.Data;

import java.util.List;

@Data
public class ReviewDto {

    private int id;

    private String startDatetime;

    private String endDatetime;

    private String status;

    List<String> ids;

}
