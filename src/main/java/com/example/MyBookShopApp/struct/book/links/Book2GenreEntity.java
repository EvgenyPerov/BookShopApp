package com.example.MyBookShopApp.struct.book.links;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Data
@ToString
@Entity
@Table(name = "book2genre")
public class Book2GenreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name ="book_id",  columnDefinition = "INT NOT NULL")
    private Integer bookId;

    @Column(name ="genre_id",  columnDefinition = "INT NOT NULL")
    private Integer genreId;


}
