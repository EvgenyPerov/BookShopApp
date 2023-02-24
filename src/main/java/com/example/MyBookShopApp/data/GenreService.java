package com.example.MyBookShopApp.data;

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

    public List<GenreEntity> getGenreMap(){
        List<GenreEntity> genreEntityList = genreRepository.findAll();

        List<GenreEntity> listResult = new ArrayList<>();

        List <GenreEntity> listForDelete = new ArrayList<>();

        for (GenreEntity jenreEntity : genreEntityList) {
            Integer id = jenreEntity.getId();

            for (GenreEntity child : genreEntityList) {
                Integer parentId = child.getParentId();
                if (parentId != null & parentId == id) {
                    child.setCountBooks(getCountBooksByGanreId(child.getId()));
                    jenreEntity.getChildren().add(child);
                    listForDelete.add(child);
                }
            }

            if (!listForDelete.contains(jenreEntity)) {
            jenreEntity.setCountBooks(getCountBooksByGanreId(id));
            listResult.add(jenreEntity);
            }
        }
        return listResult;
    }

    public Integer getCountBooksByGanreId(Integer id) {
        return getAllByGenreId(id).size();
    }

    public List<Book2GenreEntity> getAllByGenreId(Integer id){
        return book2GenreRepository.findAllByGenreIdIs(id);
    }
    public Page<Book> getPageOfGenreBooks(Integer id, Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset, limit);
        List<Integer> idBookList = new ArrayList<>();
        List<Book2GenreEntity> listIdBookByIdGenre = getAllByGenreId(id);
        for (Book2GenreEntity entity : listIdBookByIdGenre) {
           idBookList.add(entity.getBookId());
        }
        Page<Book> page = bookRepository.findBooksByIdIn(idBookList,nextPage);
        return page;

    }

}
