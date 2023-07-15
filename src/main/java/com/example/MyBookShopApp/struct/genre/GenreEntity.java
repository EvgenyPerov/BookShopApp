package com.example.MyBookShopApp.struct.genre;

import com.example.MyBookShopApp.struct.author.Author;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.links.Book2GenreEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name = "genre")
public class GenreEntity implements Comparable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "parent_id", columnDefinition = "INT")
    private Integer parentId;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String slug;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "id")
    private List<GenreEntity> children = new ArrayList<>();

    @JsonIgnore
    @Column(name = "count_books", columnDefinition = "INT")
    private Integer countBooks;

    @JsonIgnore
    @OneToMany(mappedBy = "genre")
    private List<Book2GenreEntity> book2GenreEntities = new ArrayList<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GenreEntity entity = (GenreEntity) o;
        return Objects.equals(countBooks, entity.countBooks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(countBooks);
    }

    @Override
    public int compareTo(Object o) {
        GenreEntity entity = (GenreEntity) o;
        return Integer.compare(entity.getCountBooks(), this.getCountBooks());
    }
}
