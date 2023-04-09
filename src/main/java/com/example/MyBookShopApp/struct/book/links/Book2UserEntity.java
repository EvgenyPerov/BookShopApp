package com.example.MyBookShopApp.struct.book.links;

import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.user.UserEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "book2user")
public class Book2UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "TIMESTAMP NOT NULL")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime time;

    @ManyToOne
    @JoinColumn(name = "type_id", referencedColumnName = "id")
    private Book2UserTypeEntity book2UserType;

    @ManyToOne
//    @Column(name = "book_id",columnDefinition = "INT NOT NULL")
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    @ManyToOne
//    @Column(name = "user_id",columnDefinition = "INT NOT NULL")
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;



}
