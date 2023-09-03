package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repo.*;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.struct.book.links.Book2UserTypeEntity;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
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

    public void addLookedBook(Book book, UserEntity user) {
        System.out.println("Сервис добавления книги в LOOKED");
        boolean isExist = false;
        List<Book2UserEntity> lookedBooksList = book2UserRepository.findAllByBookAndUser(book, user);
        if (!lookedBooksList.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            LocalDateTime nowDate = LocalDateTime.now();
            String formattedDateTimeNow = nowDate.format(formatter);
            System.out.println("formattedDateTimeNow - " + formattedDateTimeNow); //

            for (Book2UserEntity lookedBook : lookedBooksList) {
                LocalDateTime localDateTimeFromRepo = lookedBook.getTime();
                String formattedDateTimeFromRepo = localDateTimeFromRepo.format(formatter);
//                System.out.println("formattedDateTimeFromRepo - " + formattedDateTimeFromRepo);

                if (formattedDateTimeFromRepo.equals(formattedDateTimeNow)) {
                    isExist = true;
                    break;
                }
            }

            if (!isExist) {
                createBook2UserEntity("LOOKED", book, user);
            }
        } else {
            createBook2UserEntity("LOOKED", book, user);
        }
    }

    public boolean update(String code, Book book, UserEntity user) {
        System.out.println("Сервис обновления book2User");

//        Book2UserEntity book2UserFromRepo = book2UserRepository.findByBookAndUserIs(book, user);
        List<Book2UserEntity> book2UserEntityList = book2UserRepository.findAllByBookAndUser(book, user);

        Book2UserEntity book2UserKeptOrCart = null;
        Book2UserEntity book2UserPaidOrArchive = null;

        for (Book2UserEntity book2User : book2UserEntityList){
            book2UserPaidOrArchive = book2User.getBook2UserType().getCode().equals("PAID")
                    ||book2User.getBook2UserType().getCode().equals("ARCHIVED")
                    ? book2User : null;
            book2UserKeptOrCart = book2User.getBook2UserType().getCode().equals("KEPT")
                    ||book2User.getBook2UserType().getCode().equals("CART")
                    ? book2User : null;
        }

        if (book2UserKeptOrCart != null && book2UserPaidOrArchive == null) {
            System.out.println("В репозитории book2User уже имеется запись CART или KEPT");

            Book2UserTypeEntity typeBook = book2UserTypeRepository.findByCodeIs(code);
            book2UserKeptOrCart.setTime(LocalDateTime.now());
            book2UserKeptOrCart.setBook2UserType(typeBook);
            book2UserRepository.save(book2UserKeptOrCart);
            return true;
        }

        if (book2UserKeptOrCart == null && book2UserPaidOrArchive == null) {
            createBook2UserEntity(code, book, user);
            return true;
        }
        return false;
    }

    public void createBook2UserEntity(String code, Book book, UserEntity user) {
        Book2UserTypeEntity typeBook = book2UserTypeRepository.findByCodeIs(code);
        System.out.println("Сервис создание нового объекта book2UserEntity ");

        Book2UserEntity newBook2UserEntity = new Book2UserEntity();
        newBook2UserEntity.setTime(LocalDateTime.now());
        newBook2UserEntity.setBook2UserType(typeBook);
        newBook2UserEntity.setBook(book);
        newBook2UserEntity.setUser(user);

        book2UserRepository.save(newBook2UserEntity);
    }

    public void delete(Book book, UserEntity user) {
        System.out.println("Сервис удаления записи book2UserEntity");
        Book2UserEntity getBook2UserFromRepo = book2UserRepository.findByBookAndUserIs(book, user);
        if (getBook2UserFromRepo != null) {
            book2UserRepository.delete(getBook2UserFromRepo);
        }
    }

    public List<Book> getBooksFromRepoByTypeCodeAndUser(String bookTypeCode, UserEntity user) {
        Book2UserTypeEntity typeEntity = book2UserTypeRepository.findByCodeIs(bookTypeCode);
        List<Book2UserEntity> book2UserList = null;
        if (typeEntity != null) {
            book2UserList = book2UserRepository.findAllByBook2UserType_IdAndUser_Id(typeEntity.getId(), user.getId());
        }
        if (book2UserList != null)
            return book2UserList.stream().map(Book2UserEntity::getBook).collect(Collectors.toList());
      return new ArrayList<>();
    }

    public int getCountOfLookedBooksLastMonth(Book book){
      Book2UserTypeEntity typeEntity = book2UserTypeRepository.findByCodeIs("LOOKED");
      LocalDateTime date = LocalDateTime.now().minusMonths(1);
      return book2UserRepository.countAllByBookAndBook2UserTypeAndTimeAfter(book, typeEntity, date);
    }

    public List<Book> getLookedBooksByUserLastMonth(UserEntity user){
        Book2UserTypeEntity typeEntity = book2UserTypeRepository.findByCodeIs("LOOKED");
        LocalDateTime date = LocalDateTime.now().minusMonths(1);
        Set<Book> set = book2UserRepository
                .findAllByUserAndBook2UserTypeAndTimeAfter(user, typeEntity, date)
                .stream()
                .map(Book2UserEntity::getBook)
                .collect(Collectors.toSet());

        return new ArrayList<>(set);
    }

}

