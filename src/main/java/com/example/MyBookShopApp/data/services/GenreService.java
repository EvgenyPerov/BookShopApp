package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repo.Book2GenreRepository;
import com.example.MyBookShopApp.data.repo.BookRepository;
import com.example.MyBookShopApp.data.repo.GenreRepository;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.links.Book2GenreEntity;
import com.example.MyBookShopApp.struct.genre.GenreEntity;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GenreService {
    private BookRepository bookRepository;
    private GenreRepository genreRepository;

    private Book2GenreRepository book2GenreRepository;

    private final UserService userService;

    private final BookService bookService;

    @Autowired
    public GenreService(BookRepository bookRepository, GenreRepository genreRepository, Book2GenreRepository book2GenreRepository, UserService userService, BookService bookService) {
        this.bookRepository = bookRepository;
        this.genreRepository = genreRepository;
        this.book2GenreRepository = book2GenreRepository;
        this.userService = userService;
        this.bookService = bookService;
    }

    public String getGenreNameById(Integer id){
        return genreRepository.findGenreEntityById(id).getName();
    }

    public List<GenreEntity> getAllGenre(){return genreRepository.findAll();}

    public List<GenreEntity> getGenreList(){
        List<GenreEntity> genreEntityTotalList = genreRepository.findAll();
        List<GenreEntity> listResult = new ArrayList<>();
        List<GenreEntity> listRepeatGenresDelete = new ArrayList<>();

        if (genreEntityTotalList != null) {
            for (GenreEntity parent : genreEntityTotalList) {
                int id = parent.getId();
                parent.setCountBooks(getCountBooksByGenreId(id));

                for (GenreEntity child : genreEntityTotalList) {
                    int parentId = child.getParentId();
                    if (parentId != 0 && parentId == id) {
                        parent.getChildren().add(child);
                        listRepeatGenresDelete.add(child);
                    }
                }
                parent.getChildren().remove(0);
                if (!listRepeatGenresDelete.contains(parent) || parent.getParentId() == null) {
                    listResult.add(parent);
                }
            }
        }
        return listResult;
    }

    public List<GenreEntity> sortGenresByCountBooks(List<GenreEntity> genresList) {

        genresList.sort(Comparator.comparing(GenreEntity::getCountBooks).reversed());

        return genresList;
    }

    public int getCountBooksByGenreId(Integer genreId) {
        return book2GenreRepository.countAllByGenre_Id(genreId);
    }


    public List<Book> getPageOfGenreBooks(Integer id, Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset, limit);
        List<Integer> idBookList = new ArrayList<>();
        List<Book2GenreEntity> listIdBookByIdGenre = book2GenreRepository.findAllByGenreIdIs(id);

        for (Book2GenreEntity entity : listIdBookByIdGenre) {
           idBookList.add(entity.getBook().getId());
        }
        Page<Book> page = bookRepository.findBooksByIdIn(idBookList,nextPage);


        UserEntity user = userService.getCurrentUser();
        if (user != null) {bookService.updateStatusOfBook(page.getContent(), user);}

        return page.getContent();
    }

    public List<String> getAllGenresName(){
        List<String> list =  getAllGenre().stream()
                .map(GenreEntity::getName)
                .sorted()
                .collect(Collectors.toList());
        list.add(0,"");
        return list;
    }

}
