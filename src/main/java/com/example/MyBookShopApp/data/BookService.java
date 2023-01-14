package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.struct.book.book.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getBooksData() {
        return bookRepository.findAll();
    }

//    private String getAuthorByAuthorId(String author_id) {
//        List<Author> authors = jdbcTemplate.query("SELECT * FROM authors WHERE id = " + author_id,
//                (ResultSet rs, int rowNum) -> {
//                    Author author = new Author();
//                    author.setId(rs.getInt("id"));
//                    author.setFirstName(rs.getString("first_name"));
//                    author.setLastName(rs.getString("last_name"));
//                    return author;
//                });
//        return authors.get(0).toString();
//    }

//    public List<Book> getBooksData() {
//        List<Book> books = jdbcTemplate.query("SELECT * FROM books JOIN authors ON books.authorId = authors.id",
//                (ResultSet rs, int rowNum) -> {
//                    Book book = new Book();
//                    book.setId(rs.getInt("id"));
//                    book.setAuthor(rs.getString("last_name")+" "+rs.getString("first_name"));
//                    book.setTitle(rs.getString("title"));
//                    book.setPriceOld(rs.getString("price_Old"));
//                    book.setPrice(rs.getString("price"));
//                    return book;
//                });
//        return new ArrayList<>(books);
//    }
}
