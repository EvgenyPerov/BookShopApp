package com.example.MyBookShopApp.struct.author;

import com.example.MyBookShopApp.struct.book.book.Book;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "author")
@Data
public class Author {
    //    id INT NOT NULL AUTO_INCREMENT
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //    photo VARCHAR(255) — изображение с фотографией автора
    @Column(columnDefinition = "VARCHAR(255)")
    private String photo;

    //    slug VARCHAR(255) NOT NULL — мнемонический идентификатор автора, который будет отображаться в ссылке на его страницу
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String slug;

    //    name VARCHAR(255) NOT NULL — имя и фамилия автора
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    //    description TEXT — описание (биография, характеристика)
    @Column(columnDefinition = "TEXT")
    private String description;

//    @Column(name = "first_name")
//    private String firstName;
//    @Column(name = "last_name")
//    private String lastName;
//
//    @OneToMany(mappedBy = "author")
//    private List<Book> bookList = new ArrayList<>();


}
