package com.example.MyBookShopApp.struct.other;

import com.example.MyBookShopApp.struct.book.book.Book;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tags")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

//    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "books2tags",
    joinColumns = @JoinColumn (name = "tag_id"),
    inverseJoinColumns = @JoinColumn (name = "book_id"))
    private List<Book> bookList = new ArrayList<>();

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", nameTag='" + name + '\'' +
                '}';
    }
}
