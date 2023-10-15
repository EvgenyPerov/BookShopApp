package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.*;
import com.example.MyBookShopApp.data.services.*;
import com.example.MyBookShopApp.security.RegistrationForm;
import com.example.MyBookShopApp.struct.author.Author;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.review.BookReviewEntity;
import com.example.MyBookShopApp.struct.user.UserEntity;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Api("контроллер для администрирования")
@Controller
public class AdminController {

    @Value(value = "${uploadCovers.path}")
    private String uploadCoversPath;

    @Value(value = "${uploadPhoto.path}")
    private String uploadPhotoPath;

    private final AdminService adminService;

    private final ResourceStorage storage;

    private final AuthorService authorService;
    private final BookService bookService;
    private final UserService userService;

    private final Book2UserService book2UserService;

    private final GenreService genreService;

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private static final String NEW_BOOK_DTO = "newBookDto";
    private static final String AUTHORS = "authors";
    private static final String GENRES = "genres";
    private static final String NEW_AUTHOR_DTO = "newAuthorDto";
    private static final String NEW_USER = "usersForm";

    @Autowired
    public AdminController(AdminService adminService, ResourceStorage storage, AuthorService authorService, BookService bookService, UserService userService, Book2UserService book2UserService, GenreService genreService) {
        this.adminService = adminService;
        this.storage = storage;
        this.authorService = authorService;
        this.bookService = bookService;
        this.userService = userService;
        this.book2UserService = book2UserService;
        this.genreService = genreService;
    }

    @ModelAttribute("adminName")
    public String authenticationState(){
        UserEntity user = userService.getCurrentUser();
        return (user == null)? "unauthorized" : user.getName();
    }

    @GetMapping("/admin/book")
    public String newBookPage(Model model){
        logger.info("Переход на страницу Создания новой книги");

        var newBookDto = new BookCreateDto();

        model.addAttribute(NEW_BOOK_DTO, newBookDto);
        model.addAttribute(AUTHORS, authorService.getAllAuthorsName());
        model.addAttribute(GENRES, genreService.getAllGenresName());
        model.addAttribute("tags", bookService.getAllTagsName());

        return "/admin/newBook";
    }

    @PostMapping("/admin/book/create")
    public ModelAndView newBookData(BookCreateDto form){
        var mav = new ModelAndView();

        if (adminService.addNewBook(form)) {
            mav.addObject("createOk",true);
        }
        var newBookDto = new BookCreateDto();

        mav.addObject(NEW_BOOK_DTO, newBookDto);
        mav.addObject(AUTHORS, authorService.getAllAuthorsName());
        mav.addObject(GENRES, genreService.getAllGenresName());
        mav.addObject("tags", bookService.getAllTagsName());

        mav.setStatus(HttpStatus.OK);
        mav.setViewName("/admin/newBook");
        return mav;

    }

    @PostMapping("/admin/book/image")
    public ModelAndView saveNewBookImage(@RequestParam("file") MultipartFile file){
        logger.info("Post запрос в контроллер выбора обложки для книги");

        String savePath = storage.saveNewBookImage(file, adminService.generateSlug(), uploadCoversPath);

        var newBookDto = new BookCreateDto();
        newBookDto.setImage(savePath);

        var mav = new ModelAndView();
        mav.addObject(NEW_BOOK_DTO, newBookDto);
        mav.addObject(AUTHORS, authorService.getAllAuthorsName());
        mav.addObject(GENRES, genreService.getAllGenresName());
        mav.addObject("tags", bookService.getAllTagsName());
        mav.setStatus(HttpStatus.OK);
        mav.setViewName("/admin/newBook");
        return mav;
    }

    @GetMapping("/admin/editBook")
    public String editBook(Model model){
        logger.info("Переход на страницу редактирования книги");

        var newBookDto = new BookCreateDto();

        model.addAttribute(NEW_BOOK_DTO, newBookDto);
        model.addAttribute("books", bookService.getAllBooks().stream().sorted(Comparator.comparing(Book::getTitle)).collect(Collectors.toList()));
        model.addAttribute(AUTHORS, authorService.getAllAuthorsName());
        model.addAttribute(GENRES, genreService.getAllGenresName());
        model.addAttribute("tags", bookService.getAllTagsName());

        return "/admin/editBook";
    }

    @PostMapping("/admin/editBook/image")
    public ModelAndView editBookImage(@RequestParam("file") MultipartFile file){
        logger.info("Post запрос в контроллер выбора обложки для книги");

        String savePath = storage.saveNewBookImage(file, adminService.generateSlug(), uploadCoversPath);

        var newBookDto = new BookCreateDto();
        newBookDto.setImage(savePath);

        var mav = new ModelAndView();
        mav.addObject(NEW_BOOK_DTO, newBookDto);
        mav.addObject("books", bookService.getAllBooks().stream().sorted(Comparator.comparing(Book::getTitle)).collect(Collectors.toList()));
        mav.addObject(AUTHORS, authorService.getAllAuthorsName());
        mav.addObject(GENRES, genreService.getAllGenresName());
        mav.addObject("tags", bookService.getAllTagsName());
        mav.setStatus(HttpStatus.OK);
        mav.setViewName("/admin/editBook");
        return mav;
    }

    @PostMapping("/admin/editBook/save")
    public ModelAndView editBookData(BookCreateDto form){
        var mav = new ModelAndView();
        var newBookDto = new BookCreateDto();

        if (adminService.editBook(form)) {
            mav.addObject("updateOk",true);
        }

        mav.addObject(NEW_BOOK_DTO, newBookDto);
        mav.addObject("books", bookService.getAllBooks().stream().sorted(Comparator.comparing(Book::getTitle)).collect(Collectors.toList()));
        mav.addObject(AUTHORS, authorService.getAllAuthorsName());
        mav.addObject(GENRES, genreService.getAllGenresName());
        mav.addObject("tags", bookService.getAllTagsName());
        mav.setStatus(HttpStatus.OK);
        mav.setViewName("/admin/editBook");
        return mav;
    }

    @PostMapping("/admin/editBook/delete")
    public ModelAndView deleteBook(@RequestBody  BookCreateDto form){
        logger.info("Получен для удаления книги id: " + form.getId());
        var mav = new ModelAndView();

        adminService.deleteBook(form);

        var newBookDto = new BookCreateDto();

        mav.addObject(NEW_BOOK_DTO, newBookDto);
        mav.setStatus(HttpStatus.OK);
        mav.setViewName("/admin/editBook");
        return mav;
    }

    @GetMapping("/admin/author")
    public String getAuthor(Model model){
        logger.info("Страница создания автора");
        var newAuthorDto = new AuthorCreateDto();

        model.addAttribute(NEW_AUTHOR_DTO,newAuthorDto);

        return "/admin/newAuthor";
    }

    @PostMapping("/admin/author/photo")
    public ModelAndView saveNewAuthorPhoto(@RequestParam("file") MultipartFile file){

        String slug = adminService.generateSlug().replace("book", "author");

        String savePath = storage.saveNewBookImage(file, slug, uploadPhotoPath);
//
        var newAuthorDto = new AuthorCreateDto();
        newAuthorDto.setPhoto(savePath);

        var mav = new ModelAndView();
        mav.addObject(NEW_AUTHOR_DTO, newAuthorDto);
        mav.setStatus(HttpStatus.OK);
        mav.setViewName("/admin/newAuthor");
        return mav;
    }

    @PostMapping("/admin/author/create")
    public ModelAndView saveNewAuthor(AuthorCreateDto form){

        var mav = new ModelAndView();

        if (adminService.addNewAuthor(form)) {
            mav.addObject("createOk",true);
        }

        var newAuthorDto = new AuthorCreateDto();
        mav.addObject(NEW_AUTHOR_DTO, newAuthorDto);
        mav.setStatus(HttpStatus.OK);
        mav.setViewName("/admin/newAuthor");
        return mav;
    }

    @GetMapping("/admin/editAuthor")
    public String getEditAuthor(Model model) {
        logger.info("Страница редактирования автора");
        var newAuthorDto = new AuthorCreateDto();

        model.addAttribute(NEW_AUTHOR_DTO, newAuthorDto);
        model.addAttribute(AUTHORS, authorService.getAllAuthors().stream().sorted(Comparator.comparing(Author::getName)).collect(Collectors.toList()));
        return "/admin/editAuthor";
    }

    @PostMapping("/admin/editAuthor/save")
    public ModelAndView saveEditAuthor(AuthorCreateDto form){
        var mav = new ModelAndView();
        var newAuthorDto = new AuthorCreateDto();

        if (adminService.editAuthor(form)) {
            mav.addObject("updateOk",true);
        }

        mav.addObject(NEW_AUTHOR_DTO,newAuthorDto);
        mav.addObject(AUTHORS, authorService.getAllAuthors().stream().sorted(Comparator.comparing(Author::getName)).collect(Collectors.toList()));
        mav.setStatus(HttpStatus.OK);
        mav.setViewName("/admin/editAuthor");
        return mav;
    }

    @PostMapping("/admin/editAuthor/photo")
    public ModelAndView editAuthorPhoto(@RequestParam("file") MultipartFile file){
        logger.info("Post запрос в контроллер выбора фото автора при редактировании");

        String slug = adminService.generateSlug().replace("book", "author");

        String savePath = storage.saveNewBookImage(file, slug, uploadPhotoPath);

        var newAuthorDto = new AuthorCreateDto();
        newAuthorDto.setPhoto(savePath);

        var mav = new ModelAndView();
        mav.addObject(NEW_AUTHOR_DTO, newAuthorDto);
        mav.addObject(AUTHORS, authorService.getAllAuthors().stream().sorted(Comparator.comparing(Author::getName)).collect(Collectors.toList()));
        mav.setStatus(HttpStatus.OK);
        mav.setViewName("/admin/editAuthor");
        return mav;
    }

    @PostMapping("/admin/editAuthor/delete")
    public ModelAndView deleteAuthor(@RequestBody  AuthorCreateDto form){
        logger.info("Получен для удаления автора id: " + form.getId());
        adminService.deleteAuthor(form);
        var mav = new ModelAndView();

        var newAuthorDto = new AuthorCreateDto();
        mav.addObject(NEW_AUTHOR_DTO, newAuthorDto);
        mav.setStatus(HttpStatus.OK);
        mav.setViewName("/admin/editAuthor");
        return mav;
    }

    @GetMapping("/admin/review")
    public String getReview(Model model,
                            @RequestParam(value = "startDatetime", required = false) String startDatetime,
                            @RequestParam(value = "endDatetime", required = false) String endDatetime,
                            @RequestParam(value = "status", required = false) String status){
        logger.info("Страница управления отзывами");

        var form = new ReviewDto();
        form.setStartDatetime(startDatetime);
        form.setEndDatetime(endDatetime);
        form.setStatus(status);
        List<BookReviewEntity> reviews = userService.getReviewBetweenDate(form);
        model.addAttribute("reviews", reviews);

        return "/admin/review";
    }

    @PostMapping("/admin/review/execute")
    public String handleReviewExecute(@RequestBody ReviewDto form) {
        userService.changeStatusForReview(form);

        return "/admin/review";
    }

    @PostMapping("/admin/review/executeAll")
    public String handleReviewExecuteAll(@RequestBody ReviewDto form) {
        userService.changeStatusForAllReviews(form);

        return "/admin/review";
    }

    @GetMapping("/admin/users")
    public String getUsers(Model model){
        logger.info("Страница управления пользователями");

        var form = new RegistrationForm();
        var newBookDto = new BookCreateDto();

        model.addAttribute(NEW_USER, form);
        model.addAttribute(NEW_BOOK_DTO, newBookDto);

        return "/admin/users";
    }

    @PostMapping("/admin/users")
    public ModelAndView findUser(RegistrationForm form) {
        logger.info("POST запрос поиска пользователя");

        UserEntity user = userService.getUserByDataForm(form);

        int countBuy = book2UserService.countBuyBooksByUser(user);

        int countReject = userService.getCountReviewByUserAndStatus(user);

        var mav = new ModelAndView();

        var newForm = new RegistrationForm();
        var newBookDto = new BookCreateDto();

        mav.addObject(NEW_USER, newForm);
        mav.addObject(NEW_BOOK_DTO, newBookDto);
        mav.addObject("userFinded", user);
        mav.addObject("countReject", countReject);
        mav.addObject("countBuy", countBuy);
        mav.setStatus(HttpStatus.OK);
        mav.setViewName("/admin/users");
        return mav;
    }

    @PostMapping("/admin/users/password")
    public ModelAndView changeUser(@RequestBody RegistrationForm form) {
        logger.info("POST запрос смены пароля");

        UserEntity user = userService.getUserById(form.getId());
        userService.changeUserPassword(form, user);

        var mav = new ModelAndView();

        var newForm = new RegistrationForm();

        mav.addObject(NEW_USER, newForm);
        mav.setStatus(HttpStatus.OK);
        mav.setViewName("/admin/users");
        return mav;
    }

    @PostMapping("/admin/users/execute")
    public ModelAndView changeStatusUser(@RequestBody ReviewDto form) {
        logger.info("POST запрос блокировки/активации пользователя");


        userService.changeStatusUser(form);

        var newForm = new RegistrationForm();

        var mav = new ModelAndView();

        mav.addObject(NEW_USER, newForm);
        mav.setStatus(HttpStatus.OK);
        mav.setViewName("/admin/users");
        return mav;
    }


    @PostMapping("/admin/users/gift")
    @ResponseBody
    public Book giftBook(@RequestBody BookCreateDto form){
        logger.info("POST запрос подарка");

        return bookService.getBookByDataForm(form);
    }

    @PostMapping("/admin/users/gift/send")
    public ModelAndView changeStatusUser(@RequestBody SendGiftDto form) {
        logger.info("POST запрос сделать подарок");

        boolean isPresented = book2UserService.addGiftBookToUser(form.getIdBook(), form.getIdUser());

        var newForm = new RegistrationForm();

        var mav = new ModelAndView();

        mav.addObject(NEW_USER, newForm);

        if (isPresented) mav.setStatus(HttpStatus.OK);
            else mav.setStatus(HttpStatus.NO_CONTENT);

        mav.setViewName("/admin/users");
        return mav;
    }

    @GetMapping("/admin/registration")
    public String signUpPage(Model model){
        logger.info("Переход на страницу Регистрация Админа");
        model.addAttribute("regForm", new RegistrationForm());
        return "/admin/registration";
    }
}
