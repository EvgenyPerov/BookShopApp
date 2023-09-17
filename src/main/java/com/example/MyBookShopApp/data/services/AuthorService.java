package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repo.AuthorRepository;
import com.example.MyBookShopApp.data.repo.Book2AuthorRepository;
import com.example.MyBookShopApp.data.repo.BookRepository;
import com.example.MyBookShopApp.struct.author.Author;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.links.Book2AuthorEntity;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private AuthorRepository authorRepository;

    private BookRepository bookRepository;

    private final UserService userService;

    private final BookService bookService;

    private final Book2AuthorRepository book2AuthorRepository;

    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository, UserService userService, BookService bookService, Book2AuthorRepository book2AuthorRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.userService = userService;
        this.bookService = bookService;
        this.book2AuthorRepository = book2AuthorRepository;
    }

    public Author getAuthorById(Integer id){
        return authorRepository.findFirstByIdIs(id);
    }

    public Map<String, List<Author>> getAuthorsMap() {
        List<Author> authors = authorRepository.findAll();
        return authors.stream()
                .collect(Collectors.groupingBy((Author a) -> {return a.getName().substring(0,1);}));
    }

    public List<Book> getBooksByAuthorId(Integer authorId, Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset, limit);

        Page<Book2AuthorEntity> book2Author = book2AuthorRepository.findAllByAuthor(getAuthorById(authorId), nextPage);
        List<Book> books = book2Author.stream().map(Book2AuthorEntity :: getBook).collect(Collectors.toList());


        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            bookService.updateStatusOfBook(books, user);
        } else {
            bookService.updateStatusOfBook(books, null);
        }
        return books;
    }

    public Integer getCountBooksByAuthorId(Integer id){
        List<Book2AuthorEntity> book2Author = book2AuthorRepository.findAllByAuthor(getAuthorById(id));
        return book2Author != null? book2Author.size() : 0;
    }
}
