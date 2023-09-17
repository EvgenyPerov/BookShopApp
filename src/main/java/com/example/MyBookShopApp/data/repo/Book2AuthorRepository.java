package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.author.Author;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.links.Book2AuthorEntity;
import com.example.MyBookShopApp.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.struct.book.links.Book2UserTypeEntity;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface Book2AuthorRepository extends JpaRepository<Book2AuthorEntity, Integer> {

    List<Book2AuthorEntity> findAllByBookIn(List<Book> books);

    List<Book2AuthorEntity> findAllByAuthorIn(Set<Author> authors);

    List<Book2AuthorEntity> findAllByAuthor(Author author);

    Page<Book2AuthorEntity> findAllByAuthor(Author author, Pageable nextPage);

    List<Book2AuthorEntity> findAllByAuthor_NameContaining(String authorName);

}
