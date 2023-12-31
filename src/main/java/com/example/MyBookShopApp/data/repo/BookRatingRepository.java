package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.book.review.BookRatingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRatingRepository extends JpaRepository<BookRatingEntity, Integer> {

    List<BookRatingEntity> findAllByBookId(Integer id);


    BookRatingEntity findFirstByBookIdAndUserId(Integer bookId, Integer userId);

}
