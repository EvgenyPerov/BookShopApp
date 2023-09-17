package com.example.MyBookShopApp.struct.book.file;

import com.example.MyBookShopApp.struct.book.book.Book;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "book_file_type")
public class BookFileTypeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "bookFileTypeEntity")
    private List<BookFileEntity> bookFileList = new ArrayList<>();



}
