package com.example.MyBookShopApp.data.dto;

import com.example.MyBookShopApp.struct.payments.BalanceTransactionEntity;
import lombok.Data;

import javax.persistence.Column;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class TransactionDto {

    private String time;

    private float value;

    private String description;

}
