package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repo.*;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.struct.book.review.BookRatingEntity;
import com.example.MyBookShopApp.struct.book.review.BookReviewEntity;
import com.example.MyBookShopApp.struct.book.review.BookReviewLikeEntity;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserService {

    private UserRepository userRepository;

    private BookRatingRepository bookRatingRepository;

    private BookReviewRepository bookReviewRepository;

    private BookReviewLikeRepository bookReviewLikeRepository;

    @Autowired
    public UserService(UserRepository userRepository, BookRatingRepository bookRatingRepository, BookReviewRepository bookReviewRepository, BookReviewLikeRepository bookReviewLikeRepository) {
        this.userRepository = userRepository;
        this.bookRatingRepository = bookRatingRepository;
        this.bookReviewRepository = bookReviewRepository;
        this.bookReviewLikeRepository = bookReviewLikeRepository;
    }

    public UserEntity getUserByHash(String hash){
        return userRepository.findByHashIs(hash);
    }

    public boolean setRatingForBook(Book book, UserEntity user, Integer value) {

        if (book != null && user != null && value >= 0 && value <= 5) {
            BookRatingEntity bookRatingFromRepo = bookRatingRepository.findFirstByBookIdAndUserId(book.getId(), user.getId());

            if (bookRatingFromRepo == null) {
                bookRatingFromRepo = BookRatingEntity.builder()
                        .book(book)
                        .user(user)
                        .time(LocalDateTime.now())
                        .value(value)
                        .build();
            } else {
                bookRatingFromRepo.setTime(LocalDateTime.now());
                bookRatingFromRepo.setValue(value);
            }
            bookRatingRepository.save(bookRatingFromRepo);
            return true;
        }
        return false;
    }

    public Map<Integer, Integer> getRatingMapByBookId(Integer id){
        List<BookRatingEntity> list = bookRatingRepository.findAllByBookId(id);
        float sumOfRating = 0f;
        int count = 0;

        Map<Integer, Integer> ratingMap = new HashMap<>();
        ratingMap.put(1,0);
        ratingMap.put( 2,0);
        ratingMap.put(3,0);
        ratingMap.put(4,0);
        ratingMap.put(5,0);

        ratingMap.put(100,0);

        ratingMap.put(200,0);

        if (!list.isEmpty()) {
            for(BookRatingEntity rating  :list) {
                Integer key = rating.getValue();
                ratingMap.put(key,ratingMap.get(key) + 1);
                sumOfRating += key;
                count++;
            }
            int averageRating = Math.round(sumOfRating / count);
            ratingMap.put(100, averageRating);
            ratingMap.put(200, count);
        }
        return ratingMap;
    }

    public Map<BookReviewEntity, List<Integer>> getReviewMapByBookId(Integer id){
        Map<BookReviewEntity, List<Integer>> map = new HashMap<>();
        List<BookReviewEntity> list = bookReviewRepository.findAllByBookIdOrderByTimeDesc(id);

        for (BookReviewEntity reviewEntity : list) {
            List<Integer> likes = getReviewLikeRatingByReviewId(reviewEntity.getId());
            map.put(reviewEntity, likes);
        }

        return map;
    }

    public boolean setReviewLikeForBook(Integer reviewId, UserEntity user, short value) {

        if (user != null && value != 0) {
            BookReviewEntity review = bookReviewRepository.findByIdIs(reviewId);

            BookReviewLikeEntity bookReviewLike = bookReviewLikeRepository.findFirstByReviewIdAndUser(reviewId, user);

            if (bookReviewLike == null) {
                bookReviewLike = BookReviewLikeEntity.builder()
                        .review(review)
                        .user(user)
                        .time(LocalDateTime.now())
                        .value(value)
                        .build();
            } else {
                bookReviewLike.setTime(LocalDateTime.now());
                bookReviewLike.setValue(value);
            }
            bookReviewLikeRepository.save(bookReviewLike);
            return true;
        }
        return false;
    }

    public Integer getBookByReviewId(Integer reviewId) {
            return    bookReviewRepository.findByIdIs(reviewId).getBook().getId();
    }

    public List<Integer> getReviewLikeRatingByReviewId(Integer reviewId){
       List<BookReviewLikeEntity> list = bookReviewLikeRepository.findAllByReviewId(reviewId);
       List<Integer> likes = new ArrayList<>(3);
       int countLike = 0;
        int countDislike = 0;

        for (BookReviewLikeEntity entity : list){
            if (entity.getValue() > 0)  ++countLike; else ++countDislike;
        }
        if (countDislike > countLike) return Arrays.asList(0,countLike, countDislike);

        int dev = (countDislike == 0)? countLike / 1 : countLike / countDislike;

        if (dev >= 9) return Arrays.asList(5, countLike, countDislike);
        if (dev >= 7) return Arrays.asList(4, countLike, countDislike);
        if (dev >= 5) return Arrays.asList(3, countLike, countDislike);
        if (dev >= 3) return Arrays.asList(2, countLike, countDislike);
        if (dev >= 1) return Arrays.asList(1, countLike, countDislike);

        return Arrays.asList(0, countLike, countDislike);
    }

    public void addReviewForBook(Book book, UserEntity user, String text){
        if (user != null && !text.isBlank()) {

            BookReviewEntity review = BookReviewEntity.builder()
                    .book(book)
                    .user(user)
                    .time(LocalDateTime.now())
                    .text(text)
                    .build();
            bookReviewRepository.save(review);
        }
    }
}
