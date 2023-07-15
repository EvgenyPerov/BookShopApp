package com.example.MyBookShopApp.struct.book.review;

import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.user.UserEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book_rating")
public class BookRatingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "book_id", referencedColumnName = "id")
    private Book book;

//    @ManyToMany
//    @JoinTable(name = "books2rating",
//            joinColumns = @JoinColumn (name = "rating_id"),
//            inverseJoinColumns = @JoinColumn (name = "book_id"))
//    private List<Book> bookList = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "RatingEntity")
    private List<Book> bookList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;


    @Column(columnDefinition = "TIMESTAMP NOT NULL")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime time;

    @Column(columnDefinition = "SMALLINT NOT NULL")
    private Integer value;

//    public BookRatingEntity(Book book, UserEntity user, LocalDateTime time, Integer value) {
//        this.book = book;
//        this.user = user;
//        this.time = time;
//        this.value = value;
//    }
}
