package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.other.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.lang.annotation.Native;
import java.util.Date;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Integer> {

    @Query("FROM Book")
    List<Book> customFindAllBooks();

    List<Book> findBooksByAuthorNameContaining(String authorName);

    List<Book> findBooksByTitleContaining(String bookTitle);

    List<Book> findBooksByPriceBetween(Double min, Double max);

    List<Book> findBooksByPriceIs(Double price);

    @Query("FROM Book WHERE isBestseller = 1")
    List<Book> getBestsellers();

    @Query(value ="SELECT * FROM books WHERE discount = (SELECT MAX(discount) FROM books)", nativeQuery = true)
    List<Book> getBooksWithMaxDiscount();

    Page<Book>findBooksByTitleContaining(String bookTitle, Pageable nextPage);

    Page<Book>findBooksByPubDateBetweenOrderByPubDateDesc(Date from, Date to, Pageable nextPage);

    @Query(value ="SELECT * FROM books ORDER BY (count_of_buy + 0.7 * count_of_cart + 0.4 * count_of_postponed) DESC", nativeQuery = true)
    Page<Book> BooksRatingAndPopulatityService(Pageable nextPage);

    Page<Book> findBooksByTagListContains(Tag tag, Pageable nextPage);

    Page<Book> findBooksByIdIn(List<Integer> list, Pageable nextPage);

    Page<Book> findBooksByAuthorId(Integer auihorId, Pageable nextPage);

}
