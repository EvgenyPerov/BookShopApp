package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.file.BookFileEntity;
import com.example.MyBookShopApp.struct.book.links.Book2UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface Book2UserRepository extends JpaRepository<Book2UserEntity, Integer> {

    Book2UserEntity findByBookIdAndUserId(Integer bookId, Integer userId);

    List<Book2UserEntity> findAllByBook2UserType_IdAndUser_Id(int typeId, int userId);
}
