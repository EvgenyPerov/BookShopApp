package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.file.BookFileEntity;
import com.example.MyBookShopApp.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.struct.book.links.Book2UserTypeEntity;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface Book2UserRepository extends JpaRepository<Book2UserEntity, Integer> {

    Book2UserEntity findByBookAndUserIs(Book book, UserEntity user);

    List<Book2UserEntity> findAllByBookAndUser(Book book, UserEntity user);

    List<Book2UserEntity> findAllByBook2UserType_IdAndUser_Id(int typeId, int userId);

    int countAllByBookAndBook2UserTypeAndTimeAfter(Book book, Book2UserTypeEntity book2UserTypeEntity, LocalDateTime date);

    List<Book2UserEntity> findAllByUserAndBook2UserTypeAndTimeAfter (UserEntity user, Book2UserTypeEntity book2UserTypeEntity, LocalDateTime date);
}
