package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.links.Book2GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Book2GenreRepository extends JpaRepository<Book2GenreEntity, Integer> {

    List<Book2GenreEntity> findAllByGenreIdIs(Integer genreId);

    int countAllByGenre_Id(Integer genreId);

    List<Book2GenreEntity> findAllByBook(Book book);

}
