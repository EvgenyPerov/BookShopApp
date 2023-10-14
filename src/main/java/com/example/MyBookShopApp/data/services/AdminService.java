package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.dto.AuthorCreateDto;
import com.example.MyBookShopApp.data.dto.BookCreateDto;
import com.example.MyBookShopApp.data.repo.*;
import com.example.MyBookShopApp.struct.author.Author;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.file.BookFileEntity;
import com.example.MyBookShopApp.struct.book.file.BookFileTypeEntity;
import com.example.MyBookShopApp.struct.book.links.Book2AuthorEntity;
import com.example.MyBookShopApp.struct.book.links.Book2GenreEntity;
import com.example.MyBookShopApp.struct.genre.GenreEntity;
import com.example.MyBookShopApp.struct.other.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class AdminService {

    private final AuthorRepository authorRepository;

    private final BookRepository bookRepository;

    private final Book2AuthorRepository book2AuthorRepository;

    private final Book2GenreRepository book2GenreRepository;

    private final GenreRepository genreRepository;

    private final TagsRepository tagsRepository;

    private final BookFileRepository bookFileRepository;

    @Autowired
    private BookFileTypeRepository bookFileTypeRepository;



    public AdminService(AuthorRepository authorRepository, BookRepository bookRepository, Book2AuthorRepository book2AuthorRepository, Book2GenreRepository book2GenreRepository, GenreRepository genreRepository, TagsRepository tagsRepository, BookFileRepository bookFileRepository) throws NoSuchAlgorithmException {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
        this.book2AuthorRepository = book2AuthorRepository;
        this.book2GenreRepository = book2GenreRepository;
        this.genreRepository = genreRepository;
        this.tagsRepository = tagsRepository;
        this.bookFileRepository = bookFileRepository;
    }

    public boolean addNewBook(BookCreateDto form) {
        if (form == null) return false;

        var format = new SimpleDateFormat("yyyy-MM-dd");
        Date parsePubDate;
        try {
            parsePubDate = format.parse(form.getPubDate());
        } catch (ParseException e) {
            parsePubDate = new Date();
        }

        String slug = (form.getImage() != null && !form.getImage().isBlank()) ?
                form.getImage().substring(form.getImage().lastIndexOf('/') + 1, form.getImage().lastIndexOf('.')) : generateSlug();

        var newBook = new Book();
        newBook.setTitle(form.getTitle());
        newBook.setSlug(slug);
        newBook.setPubDate(parsePubDate);
        newBook.setPrice(form.getPrice());
        newBook.setDescription(form.getDescription());

        newBook.setIsBestseller((form.getIsBestseller() != null && form.getIsBestseller()) ? 1 : 0);

        newBook.setDiscount(form.getDiscount());
        newBook.setImage(form.getImage());

        bookRepository.save(newBook);

        var book = bookRepository.findBySlugIsIgnoreCase(slug);

        addAuthorInBook(book, form.getAuthor());

        addGenreInBook(book, form.getGenre());

        addTagInBook(book, form.getTag());

        addFileInBook(book, form.getFile());

        return true;
    }

    private void addAuthorInBook(Book book, String authorString){
        if (!authorString.isBlank()) {
            String[] list = authorString.split(",");
            for (String name : list) {
                var book2Author = new Book2AuthorEntity();
                book2Author.setBook(book);

                var author = authorRepository.findByNameIsIgnoreCase(name.trim());
                if (author != null) {
                    book2Author.setAuthor(author);
                    author.getBook2AuthorEntities().add(book2Author);

                    book2Author.setSortIndex(0);
                    book2AuthorRepository.save(book2Author);
                    book.getBook2AuthorEntities().add(book2Author);
                }

            }
        }
    }

    public void updateAuthorInBook(Book book, String authorString) {
        deleteAuthorInBook(book);

        addAuthorInBook(book, authorString);
    }

//    @Transactional
    public void deleteAuthorInBook(Book book){
        book.setBook2AuthorEntities(new ArrayList<>());

        List<Book2AuthorEntity> book2AuthorFromRepositoryByBook = book2AuthorRepository.findAllByBook(book);
        for (Book2AuthorEntity book2Author : book2AuthorFromRepositoryByBook){
            var author = book2Author.getAuthor();
            author.setBook2AuthorEntities(new ArrayList<>());
        }
        book2AuthorRepository.deleteAll(book2AuthorFromRepositoryByBook);
    }

    private void updateGenreInBook(Book book, String genreString) {
        deleteGenreInBook(book);

        addGenreInBook(book, genreString);
    }

    private void addGenreInBook(Book book, String genreString) {
        if (!genreString.isBlank()) {
            String[] list = genreString.split(",");
            for (String name : list) {

                var book2Genre = new Book2GenreEntity();
                book2Genre.setBook(book);

                GenreEntity genre = genreRepository.findByNameIs(name.trim());
                if (genre != null) {
                    book2Genre.setGenre(genre);
                    genre.getBook2GenreEntities().add(book2Genre);

                    book2GenreRepository.save(book2Genre);
                    book.getBook2GenreEntities().add(book2Genre);
                }
            }
        }
    }

    private void deleteGenreInBook(Book book) {
        book.setBook2GenreEntities(new ArrayList<>());

        List<Book2GenreEntity> book2GenreEntityList = book2GenreRepository.findAllByBook(book);
        for (Book2GenreEntity book2Genre : book2GenreEntityList){
            GenreEntity genre =book2Genre.getGenre();
            genre.setBook2GenreEntities(new ArrayList<>());
        }
        book2GenreRepository.deleteAll(book2GenreEntityList);
    }

    private void updateTagInBook(Book book, String tagString) {

        deleteTagInBook(book);

        addTagInBook(book, tagString);
    }

    private void addTagInBook(Book book, String tagString) {
        if (!tagString.isBlank()) {
            String[] list = tagString.split(",");
            for (String name : list) {
                var tag = tagsRepository.findByNameIgnoreCase(name.trim());

                if (tag != null) {
                    book.getTagList().add(tag);
                }
            }
        }
    }

    private void deleteTagInBook(Book book) {
        List<Tag> list = book.getTagList();

        if (list != null) {
            for (Tag name : list) {
                var tag = tagsRepository.findByNameIgnoreCase(name.getName());

                if (tag != null) {
                    tag.getBookList().remove(book);

                    tagsRepository.save(tag);
                }
            }
        }
        book.setTagList(new ArrayList<>());
    }

    private void addFileInBook(Book book, String fileString) {
        if (!fileString.isBlank()) {
            BookFileTypeEntity bookFileTypeEntity;
            BookFileEntity bookFile;
            String[] list = fileString.split(",");
            for (String fullName : list) {
                if (fullName != null) {

                    bookFile = new BookFileEntity();
                    bookFile.setBook(book);

                    var fileName = fullName.trim().substring(0, fullName.trim().lastIndexOf("."));

                    var size = fullName.trim().substring(fullName.trim().lastIndexOf(":")+1);

                    var type = fullName.trim().substring(fullName.trim().lastIndexOf(".") + 1, fullName.trim().indexOf(size)-1);
                    bookFileTypeEntity = bookFileTypeRepository.findByNameIsIgnoreCase(type);
                    bookFile.setBookFileTypeEntity(bookFileTypeEntity);

                    var hash = book.getSlug().replace("book", "hash");
                    bookFile.setHash(hash + "-" + type);

                    bookFile.setPath(fileName);

                    bookFile.setSize(size);

                    bookFileRepository.save(bookFile);

                    book.getBookFileList().add(bookFile);
                    bookRepository.save(book);
                    bookFileTypeEntity.getBookFileList().add(bookFile);
                }
            }
        }
    }

    private void updateFileInBook(Book book, String fileString) {

        deleteFileInBook(book);

        addFileInBook(book, fileString);
    }

    private void deleteFileInBook(Book book) {
        List<BookFileEntity> bookFileList = book.getBookFileList();
        for (BookFileEntity bookFile : bookFileList){
            bookFile.setBook(null);
            bookFile.getBookFileTypeEntity().getBookFileList().remove(bookFile);
            bookFileRepository.delete(bookFile);
        }
        book.setBookFileList(new ArrayList<>());
    }


    public boolean editBook(BookCreateDto form) {
        if (form == null) return false;

        var format = new SimpleDateFormat("yyyy-MM-dd");
        Date parsePubDate;
        try {
            parsePubDate = format.parse(form.getPubDate());
        } catch (ParseException e) {
            parsePubDate = new Date();
        }

        var book = bookRepository.getById(form.getId());

        book.setTitle(form.getTitle());
        book.setPubDate(parsePubDate);
        book.setPrice(form.getPrice());
        book.setDescription(form.getDescription());
        book.setIsBestseller((form.getIsBestseller() != null && form.getIsBestseller()) ? 1 : 0);
        book.setDiscount(form.getDiscount());
        book.setImage(form.getImage());

        updateAuthorInBook(book, form.getAuthor());

        updateGenreInBook(book, form.getGenre());

        updateTagInBook(book, form.getTag());

        updateFileInBook(book, form.getFile());

        bookRepository.save(book);

        return true;
    }

    public boolean deleteBook(BookCreateDto form) {
        if (form == null) return false;

        var book = bookRepository.getById(form.getId());
        if (book != null){
            deleteFileInBook(book);
            deleteTagInBook(book);
            deleteGenreInBook(book);
            deleteAuthorInBook(book);
            bookRepository.delete(book);
            return true;
        }
            return false;
    }


    private Random rand = SecureRandom.getInstanceStrong();
    public String generateSlug(){
        var letter = "abcdefghijklmnopqrstuvwxyz";
        var digit = "0123456789";

        var sb = new StringBuilder();
        sb.append("book-");
        for (var i=0; i<3; i++){
            char c = (char)(this.rand.nextInt(letter.length()) + 'a');
            sb.append(c);
        }
        sb.append('-');
        for (var i=0; i<3; i++){
            char c = (char)(this.rand.nextInt(digit.length()) + '0');
            sb.append(c);
        }
        return sb.toString();
    }

    public boolean addNewAuthor(AuthorCreateDto form) {
        if (form == null) return false;

        String slugTemp = (form.getPhoto() !=null && !form.getPhoto().isBlank())?
                form.getPhoto().substring(form.getPhoto().lastIndexOf('/') + 1, form.getPhoto().lastIndexOf('.')) : generateSlug();

        String slug = slugTemp.replace("book", "author");

        var author = new Author();
        author.setName(form.getName());
        author.setDescription(form.getDescription());
        author.setPhoto(form.getPhoto());
        author.setSlug(slug);
        authorRepository.save(author);
        return true;
    }

    public boolean editAuthor(AuthorCreateDto form) {
        if (form == null) return false;

        var author = authorRepository.findFirstByIdIs(form.getId());
        author.setName(form.getName());
        author.setDescription(form.getDescription());
        author.setPhoto(form.getPhoto());
        authorRepository.save(author);

        return true;
    }

    public boolean deleteAuthor(AuthorCreateDto form) {
        if (form == null) return false;

        var author = authorRepository.findFirstByIdIs(form.getId());
        if (author != null) {
            authorRepository.delete(author);
            return true;
        }
        return false;
    }

}
