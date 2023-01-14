package com.example.MyBookShopApp.data;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "test_entities")
@Data
public class TestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private  String data;


}
