package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.struct.book.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {


    @Query("FROM Book")
    List<Book> customFindAllBooks();
}
