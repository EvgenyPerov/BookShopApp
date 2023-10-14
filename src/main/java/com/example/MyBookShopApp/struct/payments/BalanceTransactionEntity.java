package com.example.MyBookShopApp.struct.payments;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "balance_transaction")
public class BalanceTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "INT NOT NULL")
    private int userId;

    @Column(columnDefinition = "TIMESTAMP NOT NULL")
    private LocalDateTime time;

    @Column(columnDefinition = "INT NOT NULL  DEFAULT 0")
    private float value;

    @Column(columnDefinition = "INT NOT NULL")
    private int bookId;

    @Column(columnDefinition = "TEXT NOT NULL")
    private String description;

    public BalanceTransactionEntity() {
        this.time = LocalDateTime.now();
    }
}
