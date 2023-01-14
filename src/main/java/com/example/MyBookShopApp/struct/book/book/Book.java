package com.example.MyBookShopApp.struct.book.book;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "book")
@Data
public class Book {
    //id INT NOT NULL AUTO_INCREMENT
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //pub_date DATE NOT NULL — дата публикации
    @Column(columnDefinition = "DATE NOT NULL")
    private Date pub_date;

    //is_bestseller TINYINT NOT NULL — книга очень популярна, является бестселлером
    @Column(columnDefinition = "SMALLINT NOT NULL")
    private boolean is_bestseller;

    //slug VARCHAR(255) NOT NULL — мнемонический идентификатор книги
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String slug;

    //title VARCHAR(255) NOT NULL — название книги
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String title;

    //image VARCHAR(255) — изображение обложки
    @Column(columnDefinition = "VARCHAR(255)")
    private String image;

    //description TEXT — описание книги
    @Column(columnDefinition = "TEXT")
    private String description;

    //price INT NOT NULL — цена в рублях основная
    @Column(columnDefinition = "INT NOT NULL")
    private int price;

    //discount TINYINT NOT NULL DEFAULT 0 — скидка в процентах или 0, если её нет
    @Column(columnDefinition = "SMALLINT NOT NULL DEFAULT 0")
    private int discount;

//    @ManyToOne
//    @JoinColumn(name = "author_id", referencedColumnName = "id")
//    private Author author;

//    @Column(name = "price_old")
//    private String priceOld;


















}
