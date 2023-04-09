package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.book.file.BookFileEntity;
import com.example.MyBookShopApp.struct.book.links.Book2UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Book2UserRepository extends JpaRepository<Book2UserEntity, Integer> {

    Book2UserEntity findByBookIdAndUserId(Integer bookId, Integer userId);
}
