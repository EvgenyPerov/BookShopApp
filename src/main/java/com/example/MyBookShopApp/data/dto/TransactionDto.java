package com.example.MyBookShopApp.data.dto;

import lombok.Data;

@Data
public class TransactionDto {

    private String time;

    private float value;

    private String description;

}
