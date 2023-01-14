package com.example.MyBookShopApp.struct.book.file;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "book_file")
@Data
public class BookFileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String hash;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String path;

    @Column(columnDefinition = "INT")
    private int type_id;
}
