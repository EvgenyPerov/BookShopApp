package com.example.MyBookShopApp.struct.book.file;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "file_download")
public class FileDownloadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "INT NOT NULL")
    private int userId;

    @Column(columnDefinition = "INT NOT NULL")
    private int bookId;

    @Column(columnDefinition = "INT NOT NULL DEFAULT 1")
    private int count = 1;

    public void increaseCount(){++count;}
}
