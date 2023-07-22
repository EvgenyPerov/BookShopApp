package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.Aop.annotations.SelectRecentDateAnnotation;
import com.example.MyBookShopApp.data.repo.BookRatingRepository;
import com.example.MyBookShopApp.data.repo.BookRepository;
import com.example.MyBookShopApp.errs.BookstoreApiWrongPatameterException;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.links.Book2GenreEntity;
import com.example.MyBookShopApp.struct.book.review.BookRatingEntity;
import com.example.MyBookShopApp.struct.genre.GenreEntity;
import com.example.MyBookShopApp.struct.other.Tag;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final UserService userService;
    private final BookRepository bookRepository;
    private final BookRatingRepository bookRatingRepository;
    private final Book2UserService book2UserService;

    @Autowired
    public BookService(UserService userService, BookRepository bookRepository, BookRatingRepository bookRatingRepository, Book2UserService book2UserService) {
        this.userService = userService;
        this.bookRepository = bookRepository;
        this.bookRatingRepository = bookRatingRepository;
        this.book2UserService = book2UserService;
    }

    public List<Book> getBooksByAuthor(String authorName){
        return bookRepository.findBooksByAuthorNameContaining(authorName);
    }

    public List<Book> getBooksByTitle(String bookTitle) throws BookstoreApiWrongPatameterException {
        if (bookTitle == null || bookTitle.isBlank() || bookTitle.length() <=1){
            throw new BookstoreApiWrongPatameterException("Wrong values passed to one or more parameters");
        } else {
            List<Book> data = bookRepository.findBooksByTitleContaining(bookTitle);
            if (data.size()>0) return data;
            else
                throw new BookstoreApiWrongPatameterException("No data found with specified parameters...");
        }
    }

    public List<Book> getBooksWithPriceBetween(Double min, Double max){
        return bookRepository.findBooksByPriceBetween(min, max);
    }

    public List<Book> getBooksWithPrice(Double price){
        return bookRepository.findBooksByPriceIs(price);
    }

    public List<Book> getBestsellers(){
        return bookRepository.getBestsellers();
    }

    public List<Book> getBooksWithMaxDiscount(){
        return bookRepository.getBooksWithMaxDiscount();
    }

    public Page<Book> getPageOfSearchResultBooks(String regex, Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset, limit);
        return bookRepository.findBooksByTitleContaining(regex, nextPage);
    }

    // этот метод срабатывает на Главной странице. Книги по умолчанию и далее при карусельной подгрузке
    public Page<Book> getRecomendedBooksOnMainPage(String postponedCookies, String cartCookies,Integer offset, Integer limit){
        Page<Book> resultPage;
        Pageable nextPage = PageRequest.of(offset, limit);

        UserEntity user = userService.getCurrentUser();
        if (user == null) {
            System.out.println("User не зарегистрирован");

            Date dateFrom = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            List<Book> getBooksByPubDateAfter = bookRepository.findAllByPubDateAfterOrderByPubDateDesc(dateFrom);

            if ((postponedCookies == null || postponedCookies.isBlank()) &&
                    (cartCookies == null || cartCookies.isBlank())) {
                System.out.println("Ничего нет в Отложенном и Корзине");

                Set<Integer> setIds = new HashSet<>();

                setIds.addAll(getBooksByPubDateAfter.stream().map(Book::getId).collect(Collectors.toSet()));

                List<BookRatingEntity> getAllRatingsForBooks = bookRatingRepository.findAll();

                Set<Integer> listBooksId = getAllRatingsForBooks.stream()
                        .map(BookRatingEntity::getBook).map(Book ::getId).collect(Collectors.toSet());

                int ratingFrom = 3;
                for (Integer id : listBooksId){
                    OptionalDouble averageRating =  getAllRatingsForBooks.stream()
                            .filter(bookRatingEntity -> bookRatingEntity.getBook().getId() == id)
                            .mapToInt(BookRatingEntity::getValue).average();
//                    System.out.println("Для книги #" + id + " средний рейтинг = " + averageRating.getAsDouble());
                    if (averageRating.isPresent()) {
                        if (averageRating.getAsDouble() >= ratingFrom) {
                            setIds.add(id);
                        }
                    }
                }

                List<Integer> listIds = new ArrayList<>();
                listIds.addAll(setIds);

                resultPage = bookRepository.findBooksByIdInOrderByPubDateDesc(listIds, nextPage);

            } else   {
                System.out.println("есть содержимое в Отложенном или Корзине");

                Set<Integer> allIdBooksThese = new HashSet<>();
                allIdBooksThese.addAll(getBooksByPubDateAfter.stream().map(Book::getId).collect(Collectors.toSet()));

                List<Book> keptBooks = getBooksFromCookies(postponedCookies);
                List<Book> cartBooks = getBooksFromCookies(cartCookies);

                Set<Integer> allKeptBooksTheseAuthors = getAllBooksIdTheseAuthors(keptBooks);
                Set<Integer> allCartBooksTheseAuthors = getAllBooksIdTheseAuthors(cartBooks);

                Set<Integer> allKeptBooksTheseTags = getAllBooksIdTheseTags(keptBooks);
                Set<Integer> allCartBooksTheseTags = getAllBooksIdTheseTags(cartBooks);

                Set<Integer> allKeptBooksTheseGenres = getAllBooksIdTheseGenre(keptBooks);
                Set<Integer> allCartBooksTheseGenres = getAllBooksIdTheseGenre(cartBooks);

                allIdBooksThese.addAll(allKeptBooksTheseAuthors);
                allIdBooksThese.addAll(allCartBooksTheseAuthors);
                allIdBooksThese.addAll(allKeptBooksTheseTags);
                allIdBooksThese.addAll(allCartBooksTheseTags);
                allIdBooksThese.addAll(allKeptBooksTheseGenres);
                allIdBooksThese.addAll(allCartBooksTheseGenres);

                List<Integer> listIds = new ArrayList<>();
                listIds.addAll(allIdBooksThese);

                List<Integer> listIdsKeptBooksForDelete = keptBooks.stream()
                                .map(Book :: getId).collect(Collectors.toList());

                listIds.removeAll(listIdsKeptBooksForDelete);

                List<Integer> listIdsCartBooksForDelete = cartBooks.stream()
                        .map(Book :: getId).collect(Collectors.toList());

                listIds.removeAll(listIdsCartBooksForDelete);

                System.out.println("Количество найденных рекомендованных книг = "+ listIds.size());
                resultPage = bookRepository.findBooksByIdInOrderByPubDateDesc(listIds, nextPage);
            }
        } else {
            System.out.println("Авторизация выполнена - " + user.getName());

            List<Book> paidBooks = book2UserService.getBooksFromRepoByTypeCodeAndUser("PAID", user);
            List<Book> keptBooks = book2UserService.getBooksFromRepoByTypeCodeAndUser("KEPT", user);
            List<Book> cartBooks = book2UserService.getBooksFromRepoByTypeCodeAndUser("CART", user);

            Set<Integer> allPaidBooksTheseAuthors = getAllBooksIdTheseAuthors(paidBooks);
            Set<Integer> allKeptBooksTheseAuthors = getAllBooksIdTheseAuthors(keptBooks);
            Set<Integer> allCartBooksTheseAuthors = getAllBooksIdTheseAuthors(cartBooks);

            Set<Integer> allPaidBooksTheseTags = getAllBooksIdTheseTags(paidBooks);
            Set<Integer> allKeptBooksTheseTags = getAllBooksIdTheseTags(keptBooks);
            Set<Integer> allCartBooksTheseTags = getAllBooksIdTheseTags(cartBooks);

            Set<Integer> allPaidBooksTheseGenres = getAllBooksIdTheseGenre(paidBooks);
            Set<Integer> allKeptBooksTheseGenres = getAllBooksIdTheseGenre(keptBooks);
            Set<Integer> allCartBooksTheseGenres = getAllBooksIdTheseGenre(cartBooks);

            Set<Integer> allIdBooksThese = new HashSet<>();
            allIdBooksThese.addAll(allPaidBooksTheseAuthors);
            allIdBooksThese.addAll(allKeptBooksTheseAuthors);
            allIdBooksThese.addAll(allCartBooksTheseAuthors);
            allIdBooksThese.addAll(allPaidBooksTheseTags);
            allIdBooksThese.addAll(allKeptBooksTheseTags);
            allIdBooksThese.addAll(allCartBooksTheseTags);
            allIdBooksThese.addAll(allPaidBooksTheseGenres);
            allIdBooksThese.addAll(allKeptBooksTheseGenres);
            allIdBooksThese.addAll(allCartBooksTheseGenres);

            List<Integer> listIds = new ArrayList<>();
            listIds.addAll(allIdBooksThese);

            List<Integer> listIdsKeptBooksForDelete = keptBooks.stream()
                    .map(Book :: getId).collect(Collectors.toList());

            listIds.removeAll(listIdsKeptBooksForDelete);

            List<Integer> listIdsCartBooksForDelete = cartBooks.stream()
                    .map(Book :: getId).collect(Collectors.toList());

            listIds.removeAll(listIdsCartBooksForDelete);

            resultPage = bookRepository.findBooksByIdInOrderByPubDateDesc(listIds, nextPage);

        }
        System.out.println("Количество подгруженных сервисом рекомендованных книг = "+ resultPage.getContent().size());
        return resultPage;
    }

    //метод срабатывает при карусельной подгрузке на Главное странице +
    // при переходе на Популярное (купленные + в корзине + отложенные  P = B + 0,7*C + 0,4*K) +
    // при карусельной подгрузке в Популярном
    public Page<Book> getPageOfPopularBooks(Integer offset, Integer limit){
        Pageable nextPage = PageRequest.of(offset, limit);
        Page<Book> page = bookRepository.BooksRatingAndPopulatityService(nextPage);
        System.out.println("Количество подгруженных сервисом популярных книг = "+ page.stream().count());
        return page;
    }

    // метод срабатывает выборе при переходе на Новинки и отображаются книги не позднее 1 месяца публикации
    // далее при карусели
    @SelectRecentDateAnnotation
    public Page<Book> getPageOfRecentBooks(String from, String to, Integer offset, Integer limit){
        SimpleDateFormat Dformat = new SimpleDateFormat("dd.MM.yyyy");
        Page<Book> page;
        Pageable nextPage;
        Date dFrom;
        Date dTo;

        if (from != null && to != null) {
            try {
                dFrom = Dformat.parse(from);
                dTo = Dformat.parse(to);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            dFrom = Date.from(LocalDate.now().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            dTo = new Date();
            System.out.println("На главной странице карусель Новинки книг даты не определены, по умолчанию  from " + dFrom +" to= " + dTo);
        }
        System.out.println("В Сервисе Новинок книг передача запроса в базу с параметрами offset = "+ offset +
                " limit= "+ limit + " from= "+ dFrom + " to= "+ dTo);
        nextPage = PageRequest.of(offset, limit);
        page = bookRepository.findBooksByPubDateBetweenOrderByPubDateDesc(dFrom, dTo, nextPage);
        System.out.println("Количество подгруженных на странице Новинок сервисом книг = " + page.stream().count());
        page.getContent().forEach(x-> System.out.println(x.getId())); //

        return page;
    }

//    public List<Book> getBooksListSortedByRating(List<BookRatingEntity> listBookRatingFromDb){
//        Map <Book, Double> mapBookAndRating = new LinkedHashMap<>();
//        List<Book> sortedList = new LinkedList<>();
//
//        if (listBookRatingFromDb != null){
//            for (BookRatingEntity ratingEntity : listBookRatingFromDb){
//                if (!mapBookAndRating.containsKey(ratingEntity.getBook())) {
//
//
//                     OptionalDouble averageValue = listBookRatingFromDb.stream()
//                            .filter(obj -> obj.getBook().getId() == ratingEntity.getBook().getId())
//                            .mapToDouble(BookRatingEntity :: getValue).average();
//
//                    mapBookAndRating.put(ratingEntity.getBook(), averageValue.getAsDouble());
//                }
//            }
//
//            // сортировка по значению по убыванию "-v.getValue()"
//            Map <Book, Double> sortedMap = mapBookAndRating.entrySet().stream()
//                    .sorted(Comparator.comparingDouble(v -> -v.getValue()))
//                    .collect(Collectors.toMap(
//                            Map.Entry :: getKey,
//                            Map.Entry :: getValue,
//                            (a,b) -> {throw new AssertionError();},
//                            LinkedHashMap ::new
//                    ));
//
//            sortedMap.values().forEach(v -> System.out.println(v));
//
//            sortedList = sortedMap.keySet().stream().collect(Collectors.toList());
//        }
//        return sortedList;
//    }

    public Book getBookById(Integer id){
        return bookRepository.findById(id).get();
    }

    public String getBookSlugById(Integer id) {
        return getBookById(id).getSlug();
    }

    public List<Book> getBooksByIdIn(String[] arrayStringId){
        List<Integer> idList = new ArrayList<>();
        if (arrayStringId !=null) {
            for (String id : arrayStringId) {
                idList.add(Integer.valueOf(id));
            }
        }
       return bookRepository.findBooksByIdIn(idList);
    }

    public void update(Book book) {
        bookRepository.save(book);
    }

    public void decreaseCart(Integer bookId) {
        Book book = bookRepository.findById(bookId).get();
        if (book != null & book.getCountOfCart() >= 0) {
            book.setCountOfCart(book.getCountOfCart() - 1);
            bookRepository.save(book);
        }
    }

    public void increaseCart(Integer bookId) {
        Book book = bookRepository.findById(bookId).get();
        if (book != null & book.getCountOfCart() >= 0) {
            book.setCountOfCart(book.getCountOfCart() + 1);
            bookRepository.save(book);
        }
    }

    public void decreaseKept(Integer bookId) {
        Book book = bookRepository.findById(bookId).get();
        if (book != null & book.getCountOfPostponed() >= 0) {
            book.setCountOfPostponed(book.getCountOfPostponed() - 1);
            bookRepository.save(book);
        }
    }

    public void increaseKept(Integer bookId) {
        Book book = bookRepository.findById(bookId).get();
        if (book != null & book.getCountOfPostponed() >= 0) {
            book.setCountOfPostponed(book.getCountOfPostponed() + 1);
            bookRepository.save(book);
        }
    }

    public float getBookCartDiscountCost(List<Book> booksFromCookie) {
        float summ = 0;
        if (booksFromCookie != null)
        for (Book book : booksFromCookie){
            summ += book.getPrice() - (book.getPrice() * book.getDiscount() / 100);
        }
        return summ;
    }


    public Object getBookCartTotalCost(List<Book> booksFromCookie) {
        float summ = 0;
        if (booksFromCookie != null)
        for (Book book : booksFromCookie){
            summ += book.getPrice();
        }
        return summ;
    }

    public String getIdListPostponedBooks(List<Book> booksFromCookie) {
        StringJoiner stringJoiner = new StringJoiner(", ");
        if (booksFromCookie != null)
        for(Book book : booksFromCookie){
            stringJoiner.add(String.valueOf(book.getId()));
        }
        System.out.println("[" + stringJoiner + "]");
        return "[" + stringJoiner + "]";
    }

    public List<Book> getBooksFromCookies(String cookies){
        List<Book> resultList = new ArrayList<>();

        if (cookies != null && !cookies.isBlank()) {
            cookies = cookies.startsWith("/") ? cookies.substring(1) : cookies;

            cookies = cookies.endsWith("/") ? cookies.substring(0, cookies.length() - 1) : cookies;

            String[] bookIdArray = cookies.split("/");

            resultList =  getBooksByIdIn(bookIdArray);
        }
        return  resultList;
    }

     Set<Integer> getAllBooksIdTheseAuthors(List<Book> statusBooks){
        Set<Integer> allBooksTheseAuthors = new HashSet<>();
        if (statusBooks != null & !statusBooks.isEmpty()) {
            Set<String> authorNameSet = statusBooks.stream().map(Book::authorName).collect(Collectors.toSet());
//            authorNameSet.forEach(System.out::println); //

            List<Book> allBooks = bookRepository.findAll();
            for (String name : authorNameSet) {
                allBooksTheseAuthors.addAll(allBooks.stream().filter(i -> i.getAuthor()
                        .getName().equalsIgnoreCase(name)).map(Book::getId).collect(Collectors.toList()));
            }
        }
        System.out.println("Найдено рекомендованных книг по авторам - "+ allBooksTheseAuthors.size());
//        allBooksByAuthor.forEach(book -> System.out.println("Книга № " +book.getId()+" Автор - "+book.getAuthor().getName())); //
        return allBooksTheseAuthors;
    }

    Set<Integer> getAllBooksIdTheseTags(List<Book> statusBooks){
        Set<Integer> allBooksTheseTags = new HashSet<>();
        if (statusBooks != null & !statusBooks.isEmpty()) {

            Set<String> tagsSet = new HashSet<>();
            for (Book book : statusBooks){
                tagsSet.addAll(book.getTagList().stream().map(Tag::getName).collect(Collectors.toSet()));
            }
//            tagsSet.forEach(System.out::println); //

            List<Book> allBooks = bookRepository.findAll();
            for (String name : tagsSet) {
                for (Book book : allBooks) {
                    for (Tag tag : book.getTagList()){
                        if (tag.getName().equalsIgnoreCase(name)){
                            allBooksTheseTags.add(book.getId());
                        }
                    }
                }
            }
        }
        System.out.println("Найдено рекомендованных книг по тэгам - "+ allBooksTheseTags.size());
//        allBooksTheseTags.forEach(book -> System.out.println(book.getTitle())); //

        return allBooksTheseTags;
    }

    Set<Integer> getAllBooksIdTheseGenre(List<Book> statusBooks){
        Set<Integer> allBooksTheseGenres = new HashSet<>();
        if (statusBooks != null & !statusBooks.isEmpty()) {

            Set<String> genreSet = new HashSet<>();
            for (Book book : statusBooks){
                genreSet.addAll( book.getBook2GenreEntities().stream().
                        map(Book2GenreEntity::getGenre).map(GenreEntity::getName).collect(Collectors.toSet()));
            }

            List<Book> allBooks = bookRepository.findAll();
            for (String name : genreSet) {
                for (Book book : allBooks) {
                    for (Book2GenreEntity book2Genre : book.getBook2GenreEntities()){
                        if (book2Genre.getGenre().getName().equalsIgnoreCase(name)){
                            allBooksTheseGenres.add(book.getId());
                        }
                    }
                }
            }
        }
        System.out.println("Найдено рекомендованных книг по жанрам - "+ allBooksTheseGenres.size());
//        allBooksTheseGenres.forEach(book -> System.out.println(book.getTitle())); //

        return allBooksTheseGenres;
    }
}
