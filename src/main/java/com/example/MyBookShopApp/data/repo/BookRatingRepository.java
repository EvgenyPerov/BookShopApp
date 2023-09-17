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

    List<BookRatingEntity> findAllByValueBetweenOrTimeAfterOrderByValueDesc(int from, int to, LocalDateTime date);

    List<BookRatingEntity> findAllByTimeAfter(LocalDateTime date);

}
