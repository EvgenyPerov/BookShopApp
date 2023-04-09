package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.book.review.BookReviewLikeEntity;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookReviewLikeRepository extends JpaRepository<BookReviewLikeEntity, Integer> {

    BookReviewLikeEntity findFirstByReviewIdAndUser(Integer reviewId, UserEntity user);

//    @Query("FROM sum(book_review_like.value) book_review_like group by review_id HAVING book_review_like.review_id = :reviewId")
//    Integer findSummReviewLikeByReviewId(Integer reviewId);

    List<BookReviewLikeEntity> findAllByReviewId(Integer reviewId);

}
