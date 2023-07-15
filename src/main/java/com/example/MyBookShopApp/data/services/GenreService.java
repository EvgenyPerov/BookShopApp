package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repo.Book2GenreRepository;
import com.example.MyBookShopApp.data.repo.BookRepository;
import com.example.MyBookShopApp.data.repo.GenreRepository;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.links.Book2GenreEntity;
import com.example.MyBookShopApp.struct.genre.GenreEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class GenreService {
    private BookRepository bookRepository;
    private GenreRepository genreRepository;

    private Book2GenreRepository book2GenreRepository;

    @Autowired
    public GenreService(BookRepository bookRepository, GenreRepository genreRepository, Book2GenreRepository book2GenreRepository) {
        this.bookRepository = bookRepository;
        this.genreRepository = genreRepository;
        this.book2GenreRepository = book2GenreRepository;
    }

    public String getGenreNameById(Integer id){
        return genreRepository.findGenreEntityById(id).getName();
    }

    public List<GenreEntity> getGenreList(){
        List<GenreEntity> genreEntityTotalList = genreRepository.findAll();
        List<GenreEntity> listResult = new ArrayList<>();
        List<GenreEntity> listRepeatGenresDelete = new ArrayList<>();

        if (genreEntityTotalList != null) {
            for (GenreEntity parent : genreEntityTotalList) {
                Integer id = parent.getId();
                parent.setCountBooks(getCountBooksByGenreId(id));

                for (GenreEntity child : genreEntityTotalList) {
                    Integer parentId = child.getParentId();
                    if (parentId != null & parentId == id) {
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


    public Page<Book> getPageOfGenreBooks(Integer id, Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset, limit);
        List<Integer> idBookList = new ArrayList<>();
        List<Book2GenreEntity> listIdBookByIdGenre = book2GenreRepository.findAllByGenreIdIs(id);

        for (Book2GenreEntity entity : listIdBookByIdGenre) {
           idBookList.add(entity.getBook().getId());
        }
        Page<Book> page = bookRepository.findBooksByIdIn(idBookList,nextPage);
        return page;

    }

}
