package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repo.*;
import com.example.MyBookShopApp.struct.author.Author;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.struct.book.links.Book2UserTypeEntity;
import com.example.MyBookShopApp.struct.book.review.BookRatingEntity;
import com.example.MyBookShopApp.struct.user.UserEntity;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class Book2UserService {

  private Book2UserRepository book2UserRepository;
  private Book2UserTypeRepository book2UserTypeRepository;

  @Autowired
    public Book2UserService(Book2UserRepository book2UserRepository, Book2UserTypeRepository book2UserTypeRepository) {
        this.book2UserRepository = book2UserRepository;
        this.book2UserTypeRepository = book2UserTypeRepository;
    }

    public void update(String typeBook, Book book, UserEntity user) {
        System.out.println("Сервис обновления book2User");
        Book2UserEntity book2UserFromRepo = book2UserRepository.findByBookIdAndUserId(book.getId(), user.getId());

        if (book2UserFromRepo != null) {
            System.out.println("В репозитории book2User уже имеется запись для " +
                    "Type = " + book2UserFromRepo.getBook2UserType().getCode() +
                    " BookId = " + book2UserFromRepo.getBook().getId() +
                    " UserID = " + book2UserFromRepo.getUser().getId());
            book2UserFromRepo.setTime(LocalDateTime.now());
            book2UserFromRepo.setBook2UserType(book2UserTypeRepository.findByCodeIs(typeBook));
            book2UserRepository.save(book2UserFromRepo);
        } else {
            Book2UserEntity book2UserEntity = new Book2UserEntity();

            book2UserEntity.setTime(LocalDateTime.now());
            book2UserEntity.setBook2UserType(book2UserTypeRepository.findByCodeIs(typeBook));
            book2UserEntity.setBook(book);
            book2UserEntity.setUser(user);
            book2UserRepository.save(book2UserEntity);
            System.out.println("Пытаемся сохранить новый объект book2UserEntity " + book2UserEntity.getBook2UserType().getCode());
        }
    }

    public void delete(Book book, UserEntity user) {
        System.out.println("Сервис удаления записи book2User");
        Book2UserEntity getBook2UserFromRepo = book2UserRepository.findByBookIdAndUserId(book.getId(), user.getId());
        if (getBook2UserFromRepo != null) {
            book2UserRepository.delete(getBook2UserFromRepo);
        }
    }

}
