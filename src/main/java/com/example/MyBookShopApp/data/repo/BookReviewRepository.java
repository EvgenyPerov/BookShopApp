package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.book.review.BookRatingEntity;
import com.example.MyBookShopApp.struct.book.review.BookReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookReviewRepository extends JpaRepository<BookReviewEntity, Integer> {

    List<BookReviewEntity> findAllByBookIdOrderByTimeDesc(Integer id);

    Integer countAllByBookId(Integer id);

    BookReviewEntity findByIdIs(Integer id);

    BookReviewEntity findFirstByBookIdAndUserId(Integer bookId, Integer userId);


}
