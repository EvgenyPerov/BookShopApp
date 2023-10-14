package com.example.MyBookShopApp.data.responses;

import lombok.Data;

@Data
public class ResponseResultOrError {
    private boolean result;
    private String error="";

}
