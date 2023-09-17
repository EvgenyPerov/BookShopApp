package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.other.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

public interface BookRepository extends JpaRepository<Book, Integer> {

    @Query("FROM Book")
    List<Book> customFindAllBooks();

    List<Book> findBooksByTitleContaining(String bookTitle);

    List<Book> findBooksByPriceBetween(Double min, Double max);

    List<Book> findBooksByPriceIs(Double price);

    @Query("FROM Book WHERE isBestseller = 1")
    List<Book> getBestsellers();

    @Query(value ="SELECT * FROM books WHERE discount = (SELECT MAX(discount) FROM books)", nativeQuery = true)
    List<Book> getBooksWithMaxDiscount();

//    @Query(value ="select b.id, avg(r.value) AS average from books AS b FULL JOIN " +
//            "book_rating AS r ON b.id = r.book_id GROUP BY b.id ORDER BY average DESC", nativeQuery = true)

//    @Query(value ="WITH rat AS (select * from books AS b FULL JOIN book_rating AS r ON b.id = r.book_id)" +
//            " SELECT b.id, avg(r.value) FROM rat GROUP BY b.id ORDER BY avg(r.value) DESC,", nativeQuery = true)

    Page<Book> findAllByTitleContainingIgnoreCase(String regex, Pageable nextPage);
    List<Book> findAllByTitleContainingIgnoreCase(String regex);

    Page<Book>findBooksByPubDateBetweenOrderByPubDateDesc(Date from, Date to, Pageable nextPage);

    List<Book> findAllByPubDateAfterOrderByPubDateDesc(Date date);

    @Query(value ="SELECT * FROM books ORDER BY (count_of_buy + 0.7 * count_of_cart + 0.4 * count_of_postponed + 0.2 * count_of_looked) DESC", nativeQuery = true)
    Page<Book> booksRatingAndPopulatityService(Pageable nextPage);

    Page<Book> findBooksByTagListContains(Tag tag, Pageable nextPage);

    Page<Book> findBooksByIdIn(List<Integer> list, Pageable nextPage);
    List<Book> findBooksByIdIn(List<Integer> list);

    List<Book> findBooksByIdIn(Set<Integer> set);

    Page<Book> findBooksByIdInOrderByPubDateDesc(List<Integer> list, Pageable nextPage);

}
