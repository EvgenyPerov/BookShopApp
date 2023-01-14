package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.struct.author.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthorService {

    private AuthorRepository authorRepository;

    @Autowired
    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    //    public Map<String, List<Author>> getAuthorsMap() {
//        List<Author> authors = jdbcTemplate.query("SELECT * FROM authors",(ResultSet rs, int rowNum) -> {
//            Author author = new Author();
//            author.setId(rs.getInt("id"));
//            author.setFirstName(rs.getString("first_name"));
//            author.setLastName(rs.getString("last_name"));
//            return author;
//        });
//
//        return authors.stream().collect(Collectors.groupingBy((Author a) -> {return a.getLastName().substring(0,1);}));
//    }
    public Map<String, List<Author>> getAuthorsMap() {
        List<Author> authors = authorRepository.findAll();
        return authors.stream()
                .collect(Collectors.groupingBy((Author a) -> {return a.getName().substring(0,1);}));
    }

}
