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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class Book2UserService {

  private Book2UserRepository book2UserRepository;
  private Book2UserTypeRepository book2UserTypeRepository;
  private final BookRepository bookRepository;

  private final UserRepository userRepository;

    private Logger logger = Logger.getLogger(this.getClass().getName());
  @Autowired
    public Book2UserService(Book2UserRepository book2UserRepository, Book2UserTypeRepository book2UserTypeRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.book2UserRepository = book2UserRepository;
        this.book2UserTypeRepository = book2UserTypeRepository;
      this.bookRepository = bookRepository;
      this.userRepository = userRepository;
  }

    public void addLookedBook(Book book, UserEntity user) {
        logger.info("Сервис добавления книги в LOOKED");
        var isExist = false;
        List<Book2UserEntity> lookedBooksList = book2UserRepository.findAllByBookAndUser(book, user);
        if (!lookedBooksList.isEmpty()) {
            var formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            var nowDate = LocalDateTime.now();
            String formattedDateTimeNow = nowDate.format(formatter);

            for (Book2UserEntity lookedBook : lookedBooksList) {
                var localDateTimeFromRepo = lookedBook.getTime();
                String formattedDateTimeFromRepo = localDateTimeFromRepo.format(formatter);

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
        logger.info("Сервис обновления book2User");

        List<Book2UserEntity> book2UserEntityList = book2UserRepository.findAllByBookAndUser(book, user);

        Book2UserEntity book2UserKeptOrCart = null;

        var isKeptOrCart = false;
        var isPaidOrArchive = false;

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
        logger.info("Сервис создание нового объекта book2UserEntity ");

        var newBook2UserEntity = new Book2UserEntity();
        newBook2UserEntity.setTime(LocalDateTime.now());
        newBook2UserEntity.setBook2UserType(typeBook);
        newBook2UserEntity.setBook(book);
        newBook2UserEntity.setUser(user);

        book2UserRepository.save(newBook2UserEntity);
    }

    public boolean delete(Book book, UserEntity user) {
        logger.info("Сервис удаления записи book2UserEntity");
        List<Book2UserEntity> book2UserList  = book2UserRepository.findAllByBookAndUser(book, user);

        Optional<Book2UserEntity> book2User = book2UserList.stream().
                filter(entity -> entity.getBook2UserType().getCode().equals("KEPT") || entity.getBook2UserType().getCode().equals("CART")).
                findFirst();

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

        if (user != null) {updateStatusOfBook(list, user);}

        return list;
    }

    public boolean addGiftBookToUser(int IdBook, int IdUser){
      Optional<Book> book = bookRepository.findById(IdBook);

        Optional<UserEntity> user = userRepository.findById(IdUser);
        if (!user.isPresent() || !book.isPresent())  return false;

        List<String> codes = Arrays.asList("PAID", "ARCHIVED");

        var Book2UserTypeList = book2UserTypeRepository.findAllByCodeIn(codes);

        var paidOrArhivedBooks = book2UserRepository.findAllByUserAndBookAndBook2UserTypeIn(user.get(), book.get(), Book2UserTypeList);

        if (paidOrArhivedBooks != null && !paidOrArhivedBooks.isEmpty()){
            return false;
        } else {
            var typeEntityPaid = book2UserTypeRepository.findByCodeIs("PAID");
            codes = Arrays.asList("KEPT", "CART");
            Book2UserTypeList = book2UserTypeRepository.findAllByCodeIn(codes);
            List<Book2UserEntity> keptOrCartBooks = book2UserRepository.findAllByUserAndBookAndBook2UserTypeIn(user.get(), book.get(), Book2UserTypeList);
            if (keptOrCartBooks != null && !keptOrCartBooks.isEmpty()){
                var book2User = keptOrCartBooks.get(0);
                book2User.setBook2UserType(typeEntityPaid);
                book2UserRepository.save(book2User);
            } else {
                var newBook2User = new Book2UserEntity();
                newBook2User.setTime(LocalDateTime.now());
                newBook2User.setBook2UserType(typeEntityPaid);
                newBook2User.setBook(book.get());
                newBook2User.setUser(user.get());
                book2UserRepository.save(newBook2User);
            }
        }
        return true;
    }

    public void updateStatusOfBook(List<Book> list, UserEntity user) {
        logger.info("Обновление статуса у книг");
        List<Book> booksForUpdate = new ArrayList<>();
        if (user != null) {
            List<Book> bookKeptList = getBooksFromRepoByTypeCodeAndUser("KEPT", user);
            List<Book> bookCartList = getBooksFromRepoByTypeCodeAndUser("CART", user);
            List<Book> bookPaidList = getBooksFromRepoByTypeCodeAndUser("PAID", user);

            for (Book book : list) {

                var isFound = false;

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
        logger.info("Обновление статуса у книги Куплено или В архиве");

            Book2UserTypeEntity typeEntityPaid = book2UserTypeRepository.findByCodeIs("PAID");
            Book2UserTypeEntity typeEntityArchived = book2UserTypeRepository.findByCodeIs("ARCHIVED");

            List<Book2UserEntity> list = book2UserRepository.findAllByBookAndUserAndBook2UserType(book, user, typeEntityPaid);
            if (list != null && !list.isEmpty()) {
                var Book2User = list.stream().findFirst();
            if (Book2User.isPresent()){
                Book2User.get().setBook2UserType(typeEntityArchived);
                book2UserRepository.save(Book2User.get());
                return true;
            }
        } else {
                list = book2UserRepository.findAllByBookAndUserAndBook2UserType(book, user, typeEntityArchived);
                var Book2User = list.stream().findFirst();
                if (Book2User.isPresent()){
                    Book2User.get().setBook2UserType(typeEntityPaid);
                    book2UserRepository.save(Book2User.get());
                    return true;
                }
            }

            return false;
        }

        public int countBuyBooksByUser(UserEntity user){
            if (user == null) return 0;

            List<String> codes = Arrays.asList("PAID", "ARCHIVED");

            var book2UserTypeList = book2UserTypeRepository.findAllByCodeIn(codes);

           return book2UserRepository.countByUserAndBook2UserTypeIn(user,  book2UserTypeList);
        }

}

