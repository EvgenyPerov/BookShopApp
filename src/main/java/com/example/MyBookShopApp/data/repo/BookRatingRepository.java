package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.links.Book2GenreEntity;
import com.example.MyBookShopApp.struct.book.review.BookRatingEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface BookRatingRepository extends JpaRepository<BookRatingEntity, Integer> {

    List<BookRatingEntity> findAllByBookId(Integer id);

    Integer countAllByBookId(Integer id);

    BookRatingEntity findFirstByBookIdAndUserId(Integer bookId, Integer userId);

//    @Query(value ="SELECT DISTINCT book_id, value FROM book_rating ORDER BY value DESC", nativeQuery = true)
//    @Query(value ="select book_id, avg(value) AS average from book_rating GROUP BY book_id ORDER BY average DESC", nativeQuery = true)
//    @Query(value ="select book_id, time from book_rating", nativeQuery = true)
//    Page<Books2RatingDto> findAllBookIdsAndRatings(Pageable nextPage);
//    LinkedHashMap<Object, Object> findAllBookIdsAndRatings();
//    List<BooksRatingDto> findAllBookIdsAndRatings();
//     Page<Integer> findAllByValueOrderByValueBookDesc findAllBookIdsAndRatings(Pageable nextPage);
//    Page<BookRatingEntity> findAllByTimeGreaterThan(LocalDateTime date, Pageable nextPage);
//    Page<BookRatingEntity> findAllByValueBetweenOrTimeAfterOrderByValueDesc(int from, int to, LocalDateTime date, Pageable nextPage);

    List<BookRatingEntity> findAllByValueBetweenOrTimeAfterOrderByValueDesc(int from, int to, LocalDateTime date);

    List<BookRatingEntity> findAllByTimeAfter(LocalDateTime date);
}
