package com.example.MyBookShopApp.struct.user;

import com.example.MyBookShopApp.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.struct.book.review.BookRatingEntity;
import com.example.MyBookShopApp.struct.book.review.BookReviewEntity;
import com.example.MyBookShopApp.struct.book.review.BookReviewLikeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String hash;

    @Column(name = "reg_time", columnDefinition = "TIMESTAMP NOT NULL")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime regTime;

    @Column(columnDefinition = "INT NOT NULL")
    private int balance;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

//    @Column(columnDefinition = "VARCHAR(255)")
    private String email;

//    @Column(columnDefinition = "VARCHAR(30)")
    private String phone;

//    @Column(columnDefinition = "VARCHAR(255)")
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Book2UserEntity> book2UserEntities = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<BookRatingEntity> bookRatingEntities = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<BookReviewEntity> bookReviewEntities = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<BookReviewLikeEntity> bookReviewLikeEntities = new ArrayList<>();
}
