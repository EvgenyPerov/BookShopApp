package com.example.MyBookShopApp.struct.book.links;

import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.genre.GenreEntity;
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

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

    @ManyToOne
    @JoinColumn(name = "genre_id", referencedColumnName = "id")
    private GenreEntity genre;

    @Override
    public String toString() {
        return "Book2GenreEntity{" +
                ", genre=" + genre +
                '}';
    }
}
