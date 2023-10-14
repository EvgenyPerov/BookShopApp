package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.book.review.BookReviewLikeEntity;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookReviewLikeRepository extends JpaRepository<BookReviewLikeEntity, Integer> {

    BookReviewLikeEntity findFirstByReviewIdAndUser(Integer reviewId, UserEntity user);

    List<BookReviewLikeEntity> findAllByReviewId(Integer reviewId);



}
