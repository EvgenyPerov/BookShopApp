package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.struct.author.Author;
import com.example.MyBookShopApp.struct.book.book.Book;
import org.springframework.beans.factory.annotation.Autowired;
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

    public AuthorService(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    public String getAuthorNameById(Integer id){
        return authorRepository.findFirstByIdIs(id).getName();
    }

    public Map<String, List<Author>> getAuthorsMap() {
        List<Author> authors = authorRepository.findAll();
        return authors.stream()
                .collect(Collectors.groupingBy((Author a) -> {return a.getName().substring(0,1);}));
    }

    public Page<Book> getBooksByAuthorId(Integer id, Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<Book> page = bookRepository.findBooksByAuthorId(id, nextPage);
        return page;
    }

    public Integer getCountBooksByAuthorId(Integer id){
        return authorRepository.findFirstByIdIs(id).getBookList().size();
    }
}
