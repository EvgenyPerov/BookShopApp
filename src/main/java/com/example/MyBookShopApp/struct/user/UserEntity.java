package com.example.MyBookShopApp.struct.user;

import com.example.MyBookShopApp.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.struct.book.review.BookRatingEntity;
import com.example.MyBookShopApp.struct.book.review.BookReviewEntity;
import com.example.MyBookShopApp.struct.book.review.BookReviewLikeEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//@Data
@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity  implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String hash;

    @Column(name = "reg_time", columnDefinition = "TIMESTAMP NOT NULL")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime regTime;

    @Column(columnDefinition = "INT NOT NULL")
    private float balance;

    @Size(min=2, message = "Не меньше 2 знаков")
    @Column(columnDefinition = "VARCHAR(255)")
    private String name;

    private String email;

    private String phone;

    @Size(min=5, message = "Не меньше 5 знаков")
    private String password;

    @Column(name = "status", columnDefinition = "SMALLINT DEFAULT 1")
    private int status;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    private Set<Role> roles= new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<Book2UserEntity> book2UserEntities = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<BookRatingEntity> bookRatingEntities = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<BookReviewEntity> bookReviewEntities = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<BookReviewLikeEntity> bookReviewLikeEntities = new ArrayList<>();

    @Override
    public String toString() {
        return "UserEntity{" +
                "id=" + id +
                ", hash='" + hash + '\'' +
                ", regTime=" + regTime +
                ", balance=" + balance +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
