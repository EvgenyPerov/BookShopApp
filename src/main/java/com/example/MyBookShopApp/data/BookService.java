package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.struct.book.book.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class BookService {

    private BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> getBooksByAuthor(String authorName){
        return bookRepository.findBooksByAuthorNameContaining(authorName);
    };

    public List<Book> getBooksByTitle(String bookTitle){
        return bookRepository.findBooksByTitleContaining(bookTitle);
    };

    public List<Book> getBooksWithPriceBetween(Double min, Double max){
        return bookRepository.findBooksByPriceBetween(min, max);
    };

    public List<Book> getBooksWithPrice(Double price){
        return bookRepository.findBooksByPriceIs(price);
    };
    public List<Book> getBestsellers(){
        return bookRepository.getBestsellers();
    };

    public List<Book> getBooksWithMaxDiscount(){
        return bookRepository.getBooksWithMaxDiscount();
    };

    public Page<Book> getPageOfSearchResultBooks(String regex, Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findBooksByTitleContaining(regex, nextPage);
    }

    // этот метод срабатывает на Главной странице. Книги по умолчанию и далее при карусельной подгрузке
    public Page<Book> geRecomendedBooksOnMainPage(Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<Book> page = bookRepository.findAll(nextPage);
        System.out.println("Количество подгруженных сервисом рекомендованных книг = "+ page.stream().count());
        return page;
    }

    // этот метод срабатывает на Главной странице. Книги по умолчанию и далее при карусельной подгрузке
    public Page<Book> getPopularBooksOnMainPage(Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<Book> page = bookRepository.findAll(nextPage);
        System.out.println("Количество подгруженных сервисом популярных книг = "+ page.stream().count());
        return page;
    }

    //метод срабатывает выборе при переходе на Популярное (купленные + в корзине + отложенные
    // P = B + 0,7*C + 0,4*K)
    // далее при карусели
    public Page<Book> getPageOfPopularBooks(Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<Book> page = bookRepository.BooksRatingAndPopulatityService(nextPage);
        System.out.println("Количество подгруженных сервисом популярных книг = "+ page.stream().count());
        return page;
    }


    // этот метод срабатывает на Главной странице. Книги по умолчанию и далее при карусельной подгрузке
    public Page<Book> getRecentedBooksOnMainPage(Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<Book> page = bookRepository.findAll(nextPage);
        System.out.println("Количество подгруженных сервисом новинок книг = "+ page.stream().count());
        return page;
    }

    // метод срабатывает выборе при переходе на Новинки и отображаются книги не позднее 1 месяца публикации
    // далее при карусели
    public Page<Book> getPageOfRecentBooks(String from, String to, Integer offset, Integer limit){
        SimpleDateFormat Dformat = new SimpleDateFormat("dd.MM.yyyy");
        Page<Book> page;
        Pageable nextPage;
        Date dFrom;
        Date dTo;

        if (from != null && to != null) {
            try {
                dFrom = Dformat.parse(from);
                dTo = Dformat.parse(to);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            dFrom = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            dTo = new Date();
        }
        nextPage = PageRequest.of(offset, limit);
        page = bookRepository.findBooksByPubDateBetweenOrderByPubDateDesc(dFrom, dTo, nextPage);
        System.out.println("Количество подгруженных сервисом Новинок книг = " + page.stream().count());
        System.out.println("На странице Новинки имеем следующие параметры запроса: from " + dFrom +" to= "
                + dTo + " offset= "+ offset + " limit= "+ limit);
        return page;
    }
}
