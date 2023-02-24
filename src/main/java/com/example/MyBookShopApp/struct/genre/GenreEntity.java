package com.example.MyBookShopApp.struct.genre;

import com.example.MyBookShopApp.struct.author.Author;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "genre")
public class GenreEntity {

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
    @ManyToOne
    @JoinColumn(name = "genre_id", referencedColumnName = "id")
    private GenreEntity genre;

    @JsonIgnore
    @OneToMany(mappedBy = "genre")
    private List<GenreEntity> children = new ArrayList<>();

    @Column(name = "count_books", columnDefinition = "INT")
    private Integer countBooks;

}
