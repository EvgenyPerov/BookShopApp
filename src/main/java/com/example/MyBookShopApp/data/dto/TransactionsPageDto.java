package com.example.MyBookShopApp.data.dto;

import com.example.MyBookShopApp.struct.payments.BalanceTransactionEntity;
import lombok.Data;
import org.springframework.format.datetime.DateFormatter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Data
public class TransactionsPageDto {

    private Integer count;
    private List<TransactionDto> transactions;

    public TransactionsPageDto(List<BalanceTransactionEntity> balanceTransactionEntities) {

        transactions = new ArrayList<>();
        for (BalanceTransactionEntity balanceTransactionEntity : balanceTransactionEntities){
            TransactionDto transaction = new TransactionDto();

            Timestamp timestamp = new Timestamp(balanceTransactionEntity.getTime().toInstant(ZoneOffset.of("+04:00")).toEpochMilli());
            transaction.setTime(String.valueOf(timestamp.getTime()));
            transaction.setValue(balanceTransactionEntity.getValue());
            transaction.setDescription(balanceTransactionEntity.getDescription());
            transactions.add(transaction);
        }

        this.count = transactions.size();
    }


}
