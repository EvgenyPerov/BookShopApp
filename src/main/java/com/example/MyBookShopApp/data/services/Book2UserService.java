package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repo.*;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.links.Book2UserEntity;
import com.example.MyBookShopApp.struct.book.links.Book2UserTypeEntity;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class Book2UserService {

  private Book2UserRepository book2UserRepository;
  private Book2UserTypeRepository book2UserTypeRepository;
  private final BookRepository bookRepository;

  @Autowired
    public Book2UserService(Book2UserRepository book2UserRepository, Book2UserTypeRepository book2UserTypeRepository, BookRepository bookRepository) {
        this.book2UserRepository = book2UserRepository;
        this.book2UserTypeRepository = book2UserTypeRepository;
      this.bookRepository = bookRepository;
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

        List<Book2UserEntity> book2UserEntityList = book2UserRepository.findAllByBookAndUser(book, user);

        Book2UserEntity book2UserKeptOrCart = null;

        boolean isKeptOrCart = false;
        boolean isPaidOrArchive = false;

        for (Book2UserEntity book2User : book2UserEntityList){
            if (book2User.getBook2UserType().getCode().equals("PAID") ||book2User.getBook2UserType().getCode().equals("ARCHIVED")) {
                isPaidOrArchive = true;
            }
            if (book2User.getBook2UserType().getCode().equals("KEPT") ||book2User.getBook2UserType().getCode().equals("CART")) {
                isKeptOrCart = true;
                book2UserKeptOrCart = book2User;
            }
        }

        if (isKeptOrCart && !isPaidOrArchive) {
            System.out.println("В репозитории book2User уже имеется запись CART или KEPT");
            Book2UserTypeEntity typeBook = book2UserTypeRepository.findByCodeIs(code);
            book2UserKeptOrCart.setTime(LocalDateTime.now());
            book2UserKeptOrCart.setBook2UserType(typeBook);
            book2UserRepository.save(book2UserKeptOrCart);
            return true;
        }

        if (!isKeptOrCart  && !isPaidOrArchive) {
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

    public boolean delete(Book book, UserEntity user) {
        System.out.println("Сервис удаления записи book2UserEntity");
        List<Book2UserEntity> book2UserList  = book2UserRepository.findAllByBookAndUser(book, user);

        Optional<Book2UserEntity> book2User = book2UserList.stream().
                filter(entity -> entity.getBook2UserType().getCode().equals("KEPT") || entity.getBook2UserType().getCode().equals("CART")).
                findFirst();

//        Book2UserEntity getBook2UserFromRepo = book2UserRepository.findByBookAndUserIs(book, user);
        if (book2User.isPresent()) {
            book2UserRepository.delete(book2User.get());
            return true;
        }
        return false;
    }

    public List<Book> getBooksFromRepoByTypeCodeAndUser(String bookTypeCode, UserEntity user) {
        Book2UserTypeEntity typeEntity = book2UserTypeRepository.findByCodeIs(bookTypeCode);
        List<Book2UserEntity> book2UserList = book2UserRepository.findAllByBook2UserTypeAndUser(typeEntity, user);

        if (book2UserList != null) {
            List<Book> list = book2UserList.stream().map(Book2UserEntity::getBook).collect(Collectors.toList());
            return list;
        }
      return new ArrayList<>();
    }

    public List<Book> getPageOfBooksFromRepoByTypeCodeAndUser(Integer offset, Integer limit, String bookTypeCode, UserEntity user) {
        Pageable nextPage = PageRequest.of(offset, limit);
        Book2UserTypeEntity typeEntity = book2UserTypeRepository.findByCodeIs(bookTypeCode);
        List<Book2UserEntity> book2UserList = book2UserRepository.findAllByBook2UserTypeAndUser(typeEntity, user, nextPage).getContent();

        if (book2UserList != null) {
            List<Book> list = book2UserList.stream().map(Book2UserEntity::getBook).collect(Collectors.toList());
            if (user != null) {updateStatusOfBook(list, user);}
            return list;
        }
        return new ArrayList<>();
    }

    public int getCountOfLookedBooksLastMonth(Book book){
      Book2UserTypeEntity typeEntity = book2UserTypeRepository.findByCodeIs("LOOKED");
      LocalDateTime date = LocalDateTime.now().minusMonths(1);
      return book2UserRepository.countAllByBookAndBook2UserTypeAndTimeAfter(book, typeEntity, date);
    }


    public List<Book> getPageOfLookedBooksByUserLastMonth(Integer offset, Integer limit, UserEntity user){
        Pageable nextPage = PageRequest.of(offset, limit);

        Book2UserTypeEntity typeEntity = book2UserTypeRepository.findByCodeIs("LOOKED");
        LocalDateTime date = LocalDateTime.now().minusMonths(1);
        List<Book> list = book2UserRepository
                .findAllByUserAndBook2UserTypeAndTimeAfter(user, typeEntity, date, nextPage)
                .stream()
                .map(Book2UserEntity::getBook)
                .collect(Collectors.toList());
        System.out.println("Количество подгруженных сервисом просмотренных книг = "+ list.size()); //

        if (user != null) {updateStatusOfBook(list, user);}

        return list;
    }

    public void updateStatusOfBook(List<Book> list, UserEntity user) {
        System.out.println("Обновление статуса у книг");
        List<Book> booksForUpdate = new ArrayList<>();
        if (user != null) {
            List<Book> bookKeptList = getBooksFromRepoByTypeCodeAndUser("KEPT", user);
            List<Book> bookCartList = getBooksFromRepoByTypeCodeAndUser("CART", user);
            List<Book> bookPaidList = getBooksFromRepoByTypeCodeAndUser("PAID", user);

            for (Book book : list) {

                boolean isFound = false;

                if (bookKeptList.contains(book)) {
                    book.setStatus("KEPT");
                    booksForUpdate.add(book);
                    isFound = true;
                }
                if (bookCartList.contains(book)) {
                    book.setStatus("CART");
                    booksForUpdate.add(book);
                    isFound = true;
                }
                if (bookPaidList.contains(book)) {
                    book.setStatus("PAID");
                    booksForUpdate.add(book);
                    isFound = true;
                }
                if (!isFound && book.getStatus() != null) {
                    book.setStatus(null);
                    booksForUpdate.add(book);
                }
            }
        } else {
            for (Book book : list) {
                if (book.getStatus() != null) {
                    book.setStatus(null);
                    booksForUpdate.add(book);
                }
            }
        }
        if (!booksForUpdate.isEmpty()) {
            bookRepository.saveAll(booksForUpdate);
        }
    }

    public boolean updateStatusOfBookPaidOrArchived(Book book, UserEntity user) {
        System.out.println("Обновление статуса у книги Куплено или В архиве");

            Book2UserTypeEntity typeEntityPaid = book2UserTypeRepository.findByCodeIs("PAID");
            Book2UserTypeEntity typeEntityArchived = book2UserTypeRepository.findByCodeIs("ARCHIVED");

            List<Book2UserEntity> list = book2UserRepository.findAllByBookAndUserAndBook2UserType(book, user, typeEntityPaid);
            if (list != null && !list.isEmpty()) {
                Optional<Book2UserEntity> Book2User = list.stream().findFirst();
            if (Book2User.isPresent()){
                Book2User.get().setBook2UserType(typeEntityArchived);
                book2UserRepository.save(Book2User.get());
                return true;
            }
        } else {
                list = book2UserRepository.findAllByBookAndUserAndBook2UserType(book, user, typeEntityArchived);
                Optional<Book2UserEntity> Book2User = list.stream().findFirst();
                if (Book2User.isPresent()){
                    Book2User.get().setBook2UserType(typeEntityPaid);
                    book2UserRepository.save(Book2User.get());
                    return true;
                }
            }

            return false;
        }


}

