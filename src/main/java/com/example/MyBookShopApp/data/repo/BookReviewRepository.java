package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.review.BookReviewEntity;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookReviewRepository extends JpaRepository<BookReviewEntity, Integer> {

    List<BookReviewEntity>  findAllByBookAndIsCheckedInOrderByTimeDesc(Book book, List<Integer> statuses);


    BookReviewEntity findByIdIs(Integer id);

    List<BookReviewEntity> findAllByIdIn(List<Integer> ids);

    List<BookReviewEntity> findAllByIsCheckedInAndTimeBetweenOrderByTime(List<Integer> statuses, LocalDateTime from, LocalDateTime to);

    int countByUserAndIsChecked(UserEntity user, int isChecked);
}
