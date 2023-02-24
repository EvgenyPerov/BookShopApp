package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.struct.book.book.Book;
import lombok.Data;

import java.util.List;
@Data
public class BooksPageDto {

    private Integer count;
    private List<Book> books;

    public BooksPageDto(List<Book> books) {
        this.books = books;
        this.count = books.size();
    }
}
