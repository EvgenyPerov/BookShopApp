package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.struct.author.Author;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.links.Book2AuthorEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class BookRepositoryTests {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private Book2AuthorRepository book2AuthorRepository;
    @Autowired
    private BookService bookService;
    @Autowired
    private AuthorRepository authorRepository;

    @Test
    @DisplayName("Поиск книг по имени автора")
    void findBooksByAuthorNameContainingTest() {
        String token  = "Christin";
        List<Book> books = bookService.getBooksByAuthorContaining(token);

        Assertions.assertNotNull(books);
        Assertions.assertTrue(books.size()>0);

        if (books != null){
            System.out.println("Для автора " + token + " найдено всего книг - " + books.size());
            for (Book book : books){
                Assertions.assertTrue(book.allAuthorsNameString().contains(token));
            }
        }
    }

    @Test
    @DisplayName("Поиск книг по названию")
    void findBooksByTitleContainingTest() {
        String token  = "Born";
        List<Book> books = bookRepository.findBooksByTitleContaining(token);

        Assertions.assertNotNull(books);
        Assertions.assertFalse(books.isEmpty());

        if (books != null){
            System.out.println("По названию книги " + token + " найдено всего книг - " + books.size());
            for (Book book : books){
                System.out.println(book.getId() + " " + book.getTitle());
                Assertions.assertTrue(book.getTitle().contains(token));
            }
        }
    }

    @Test
    @DisplayName("Поиск книг - Bestsellers")
    void getBestsellersTest() {
        List<Book> books = bookRepository.getBestsellers();

        Assertions.assertNotNull(books);
        Assertions.assertTrue(books.size()>0);

        if (books != null){
            System.out.println("Bestsellers найдено всего книг - " + books.size());
            for (Book book : books){
                System.out.println(book.getId() + " - " + book.getTitle());
                Assertions.assertEquals(book.getIsBestseller(), 1);
            }
        }
    }
}