package com.example.MyBookShopApp.struct.author;

import com.example.MyBookShopApp.struct.book.book.Book;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authors")
@Data
@ToString
public class Author {
    //    id INT NOT NULL AUTO_INCREMENT
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "this is generated by DB", position = 1)
    private Integer id;

    //    photo VARCHAR(255) — изображение с фотографией автора
    @Column(columnDefinition = "VARCHAR(255)")
    private String photo;

    //    slug VARCHAR(255) NOT NULL — мнемонический идентификатор автора, который будет отображаться в ссылке на его страницу
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String slug;

    //    name VARCHAR(255) NOT NULL — имя и фамилия автора
    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    @ApiModelProperty(value = "this is name of author", example = "Bob Marley", position = 2)
    private String name;

    //    description TEXT — описание (биография, характеристика)
    @Column(columnDefinition = "TEXT")
    @ApiModelProperty("this is description of author")
    private String description;

    @JsonIgnore
    @OneToMany(mappedBy = "author")
    private List<Book> bookList = new ArrayList<>();

    public Author() {}

    public Author(List<String> authors) {
        if (authors != null && !authors.isEmpty()){
            this.name = authors.toString();
        }
    }

    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", photo='" + photo + '\'' +
                ", slug='" + slug + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
//                ", bookList=" + bookList +
                '}';
    }
}
