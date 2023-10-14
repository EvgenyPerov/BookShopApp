package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.struct.book.links.Book2UserTypeEntity;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface Book2UserRepository extends JpaRepository<Book2UserEntity, Integer> {

    Book2UserEntity findByBookAndUserIs(Book book, UserEntity user);
    List<Book2UserEntity> findAllByBookAndUserAndBook2UserType(Book book, UserEntity user, Book2UserTypeEntity book2UserTypeEntity);

    List<Book2UserEntity> findAllByBookAndUser(Book book, UserEntity user);

    List<Book2UserEntity> findAllByBook2UserTypeAndUser(Book2UserTypeEntity book2UserTypeEntity, UserEntity user);

    Page<Book2UserEntity> findAllByBook2UserTypeAndUser(Book2UserTypeEntity book2UserTypeEntity, UserEntity user, Pageable nextPage);

    int countAllByBookAndBook2UserTypeAndTimeAfter(Book book, Book2UserTypeEntity book2UserTypeEntity, LocalDateTime date);

    Page<Book2UserEntity> findAllByUserAndBook2UserTypeAndTimeAfter (UserEntity user, Book2UserTypeEntity book2UserTypeEntity, LocalDateTime date, Pageable nextPage);

    int countByUserAndBook2UserTypeIn(UserEntity user, List<Book2UserTypeEntity> book2UserTypeEntityList);

    List<Book2UserEntity> findAllByUserAndBookAndBook2UserTypeIn(UserEntity user, Book book, List<Book2UserTypeEntity> book2UserTypeEntityList);
}
