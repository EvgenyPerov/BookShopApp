package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.struct.book.links.Book2UserTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Book2UserTypeRepository extends JpaRepository<Book2UserTypeEntity, Integer> {

    Book2UserTypeEntity findByCodeIs(String code);
}
