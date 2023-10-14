package com.example.MyBookShopApp.controllers;

import java.util.List;
import java.util.stream.Collectors;

import com.example.MyBookShopApp.data.services.AuthorService;
import com.example.MyBookShopApp.struct.author.Author;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("states")
public class AuthorRestController {

    @Autowired
    private AuthorService authorService;

    @GetMapping
    public List<String> stateItems(@RequestParam(value = "q", required = false) String query) {
        if (query.isBlank()) {
            return authorService.getAllAuthors().stream()
//                    .limit(15)
//                    .map(this::mapToAuthor)
                    .map(Author :: getName)
                    .collect(Collectors.toList());
        }

        return authorService.getAllAuthors().stream()
                .filter(author -> author.getName()
                        .toLowerCase()
                        .contains(query))
                .map(Author :: getName)
                .collect(Collectors.toList());
    }


}
