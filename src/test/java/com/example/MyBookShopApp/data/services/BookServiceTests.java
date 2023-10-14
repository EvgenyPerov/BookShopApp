package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repo.Book2AuthorRepository;
import com.example.MyBookShopApp.data.repo.BookRatingRepository;
import com.example.MyBookShopApp.data.repo.BookRepository;
import com.example.MyBookShopApp.struct.author.Author;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.links.Book2AuthorEntity;
import com.example.MyBookShopApp.struct.book.links.Book2GenreEntity;
import com.example.MyBookShopApp.struct.book.review.BookRatingEntity;
import com.example.MyBookShopApp.struct.genre.GenreEntity;
import com.example.MyBookShopApp.struct.other.Tag;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@SpringBootTest
class BookServiceTests {

    @Autowired
    private BookService bookService;

    @Autowired
    private  BookRepository bookRepository;

    @Autowired
    private BookRatingRepository bookRatingRepository;

    @Autowired
    private Book2AuthorRepository book2AuthorRepository;

    @Test
    @DisplayName("Получение книг по популярности")
    void getPageOfPopularBooksTest() {
        List<Book> bookList = bookService.getPageOfPopularBooks(0,6);

        if (bookList.size()==1) System.out.println("В списке всего 1 книга, что не достаточно для сравнения");

        if (bookList.size()>1) {
            for (int i = 0; i < bookList.size()-1; i++) {
                Book book1 = bookList.get(i);
                Book book2 = bookList.get(i+1);
                double popCurrent = book1.getCountOfBuy() + 0.7 * book1.getCountOfCart() + 0.4 * book1.getCountOfPostponed() + 0.2 * book1.getCountOfLooked();
                double popNext = book2.getCountOfBuy() + 0.7 * book2.getCountOfCart() + 0.4 * book2.getCountOfPostponed() + 0.2 * book2.getCountOfLooked();
                Assertions.assertTrue(popCurrent > popNext);
            }
        }
    }

    @Test
    @DisplayName("Получение рекомендованных книг для User = Null в Отложенном и Корзине - пусто")
    void getRecomendedBooksOnMainPageTest() {
        String postponedCookies = "";
        String cartCookies = "";
        int offset = 0;
        int limit = 20;

        List<Book> booksActual = bookService.getRecomendedBooksOnMainPage(postponedCookies, cartCookies, offset, limit);
        int countOfBooksTotal = booksActual.size();

        // проверка по дате публикации
        Date dateFrom = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Book> booksByPubDateAfterExtended = bookRepository.findAllByPubDateAfterOrderByPubDateDesc(dateFrom);
        System.out.println("Список booksByPubDateAfterExtended имеет размер - " + booksByPubDateAfterExtended.size());

        if (booksByPubDateAfterExtended.size() >= countOfBooksTotal) {
            List<Book> booksOnlyRecent = booksByPubDateAfterExtended.subList(0, countOfBooksTotal);

            Assertions.assertTrue(booksActual.containsAll(booksOnlyRecent));

        } else{
            Assertions.assertTrue(booksActual.containsAll(booksByPubDateAfterExtended));

            // дальше будет проверка за вычетом книг новинок, проверенных по дате публикации
            List<Book> booksWithoutRecent = booksActual.subList(booksByPubDateAfterExtended.size(), countOfBooksTotal);
            System.out.println("Список booksWithoutRecent имеет размер - " + booksWithoutRecent.size());

                List<BookRatingEntity> getAllRatingsForBooks = bookRatingRepository.findAll();

                //проверка оставшихся книг по рэйтингу
                Set<Integer> listBooksId = booksWithoutRecent.stream().map(Book::getId).collect(Collectors.toSet());

                for (Integer id : listBooksId) {
                    OptionalDouble averageRating = getAllRatingsForBooks.stream()
                            .filter(bookRatingEntity -> bookRatingEntity.getBook().getId() == id)
                            .mapToInt(BookRatingEntity::getValue).average();

                    int ratingFrom = 3;
                    if (averageRating.isPresent()) {
                    Assertions.assertTrue (averageRating.getAsDouble() >= ratingFrom);
                    }
                }


        }
    }

    @Test
    @DisplayName("Получение рекомендованных книг из Cookies")
    void getBooksFromCookiesTest(){
        String cookies = "1";
        List<Book> booksActual =  bookService.getBooksFromCookies(cookies);
        Book bookExpected =  bookService.getBookById(Integer.valueOf(cookies));
        Assertions.assertEquals(booksActual.get(0), bookExpected);

        cookies = "1/2/3";
        booksActual =  bookService.getBooksFromCookies(cookies);
        String[] cookiesArray = new String[]{"1","2","3"};
        List<Book> bookExpected2 = bookService.getBooksByIdIn(cookiesArray);
        Assertions.assertEquals(booksActual, bookExpected2);

        Set<Integer> allKeptBooksTheseAuthors = bookService.getAllBooksIdTheseAuthors(bookExpected2);

    }

    @Test
    @DisplayName("Получение рекомендованных книг по авторам")
    void getAllBooksIdTheseAuthorsTest() {
        List<Book> books = new ArrayList<>();
        // берем 1, 3, 5 книги
        // будут найдены авторы 10,11,14,15,16,20 = 6 авторов
        // у этих авторов будет найдено 30 книг
        for (int i = 2; i <= 6; i += 2) {
            books.add(bookService.getBookById(i));
        }
        // получить Id всех книг, написанных авторами переданных трех книг
        Set<Integer> booksIdTheseAuthorsActual = bookService.getAllBooksIdTheseAuthors(books);

        Set<Author> authorSet = new HashSet<>();
        books.forEach(book -> authorSet.addAll(book.allAuthorsList()));

        List<Book2AuthorEntity> book2Author = book2AuthorRepository.findAllByAuthorIn(authorSet);
        Set<Integer> booksIdTheseAuthorsExpected = book2Author.stream().map(Book2AuthorEntity::getBook).map(Book::getId).collect(Collectors.toSet());

        Assertions.assertEquals(booksIdTheseAuthorsActual, booksIdTheseAuthorsExpected);
    }
        @Test
        @DisplayName("Получение рекомендованных книг по жанрам")
        @Transactional
        void getAllBooksIdTheseGenreTest(){
            List<Book> books = new ArrayList<>();
            // берем 1, 3, 5 книги
            for (int i = 2; i <= 6; i += 2) {
                books.add(bookService.getBookById(i));
            }
            books.forEach(System.out::println); //
            // получить Id всех книг, по жанрам переданных трех книг
            Set<Integer> IdBooksTheseGenreActual = bookService.getAllBooksIdTheseAuthors(books);

            List<Book> booksTheseGenreActual = bookRepository.findBooksByIdIn(IdBooksTheseGenreActual);

            Set<String> genreNameSetExpected = new HashSet<>();
            for (Book book : books){
                genreNameSetExpected.addAll( book.getBook2GenreEntities().stream().
                        map(Book2GenreEntity::getGenre).map(GenreEntity::getName).collect(Collectors.toSet()));
            }

            List<String> genreNameSetActual = new ArrayList<>();
            for (Book book : booksTheseGenreActual){
                genreNameSetActual.addAll( book.getBook2GenreEntities().stream().
                        map(Book2GenreEntity::getGenre).map(GenreEntity::getName).collect(Collectors.toList()));
            }

            genreNameSetExpected.forEach(genreName -> Assertions.assertTrue(genreNameSetActual.contains(genreName)));
    }

    @Test
    @DisplayName("Получение рекомендованных книг по тэгам")
    @Transactional
    void getAllBooksIdTheseTagsTest(){
        List<Book> books = new ArrayList<>();
        // берем 1, 3, 5 книги
        for (int i = 2; i <= 6; i += 2) {
            books.add(bookService.getBookById(i));
        }
        books.forEach(System.out::println); //

        // получить тэги 3 книг
        Set<String> tagsNameSetExpected = new HashSet<>();
        for (Book book : books){
            tagsNameSetExpected.addAll(book.getTagList().stream().map(Tag::getName).collect(Collectors.toSet()));
        }

        // получить Id всех книг, по жанрам переданных трех книг
        Set<Integer> IdBooksTheseTagActual = bookService.getAllBooksIdTheseTags(books);

        List<Book> booksTheseTagActual = bookRepository.findBooksByIdIn(IdBooksTheseTagActual);

        // получить тэги всех книг
        Set<String> tagsNameSetActual = new HashSet<>();
        for (Book book : booksTheseTagActual){
            tagsNameSetActual.addAll(book.getTagList().stream().map(Tag::getName).collect(Collectors.toSet()));
        }
        tagsNameSetExpected.forEach(tagName -> Assertions.assertTrue(tagsNameSetActual.contains(tagName)));
    }

}