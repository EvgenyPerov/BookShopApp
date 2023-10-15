package com.example.MyBookShopApp.controllers;

import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.dto.ContactMessageDto;
import com.example.MyBookShopApp.data.responses.ApiResponse;
import com.example.MyBookShopApp.data.services.*;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.errs.PayException;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.user.UserEntity;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Logger;

@Controller
public class UserServiceController {

    private final UserService userService;

    private final BookService bookService;

    private final Book2UserService book2UserService;

    private final PaymentServise paymentServise;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    public UserServiceController(UserService userService, BookService bookService, Book2UserService book2UserService, PaymentServise paymentServise) {
        this.userService = userService;
        this.bookService = bookService;
        this.book2UserService = book2UserService;
        this.paymentServise = paymentServise;
    }

    @ModelAttribute("searchWordDto")
    public SearchWordDto searchWordDto(){
        return new SearchWordDto();
    }

    @ModelAttribute("state")
    public String authenticationState(){
        return (userService.getCurrentUser() == null)? "unauthorized" : "authorized";
    }

    @ModelAttribute("myBooks")
    public int geBookPostponedList() {
        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            return book2UserService.getBooksFromRepoByTypeCodeAndUser("PAID", user).size();
        } else {
            return 0;
        }
    }

    @ModelAttribute("bookPostponedList")
    public List<Book> geBookPostponedList(@CookieValue(name = "postponedContents", required = false) String postponedContents){
        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            return book2UserService.getBooksFromRepoByTypeCodeAndUser("KEPT", user);
        } else {
            return bookService.getBooksFromCookies(postponedContents);
        }
    }

    @ModelAttribute("bookCartList")
    public List<Book> geBookCartList(@CookieValue(name = "cartContents", required = false) String cartContents){
        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            return book2UserService.getBooksFromRepoByTypeCodeAndUser("CART",user);
        } else {
            return bookService.getBooksFromCookies(cartContents);
        }
    }

    @ModelAttribute("contactMessageDto")
    public ContactMessageDto handleContactMessageDto(){
        return new ContactMessageDto();
    }

    @GetMapping("/documents/index")
    public String documentsPage(Model model){
        logger.info("Переход на страницу Документы");
        model.addAttribute("documents",bookService.getAllDocuments());
        return "/documents/index";
    }

    @GetMapping("/documents/{slug}")
    public String documentsPage(Model model, @PathVariable(value = "slug", required = false) String slug){
        logger.info("Переход на страницу Конкретного документа");
        model.addAttribute("doc",bookService.getDocumentBySlug(slug));
        return "/documents/" + slug;
    }

    @GetMapping("/about")
    public String aboutCompanyPage(){
        logger.info("Переход на страницу О компании");
        return "/about";
    }

    @GetMapping("/faq")
    public String faqPage(Model model){
        logger.info("Переход на страницу Помощь");
        model.addAttribute("faqMap",bookService.getMapAllFaq());
        return "/faq";
    }

    @GetMapping("/contacts")
    public String contactsPage(){
        logger.info("Переход на страницу Контакты");
        return "/contacts";
    }

    @PostMapping("/contacts")
    public String sendMessageToContacts(ContactMessageDto form){
        logger.info("Отправка сообщения в поддержку");
        userService.addMessageToSupport(form);
        return "redirect:/contacts";
    }

    @ApiOperation("этот метод изначально при переходе с главной страницы на Тэг")
    @GetMapping("/tags/index/{tagId}")
    public String getTags(Model model,
                          @PathVariable(value = "tagId", required = false) Integer tagId) {
        logger.info("Переход на страницу тэгов");
        List<Book> list = bookService.getPageOfTagBooks(tagId, 0, 20);
        model.addAttribute("bookListByTag",list);
        model.addAttribute("tagName",bookService.getTagNameById(tagId));
        model.addAttribute("id",tagId);
    return "/tags/index";
    }

    @ApiOperation("этот метод постраничная загрузка книг найденных по Тэгу")
    @ResponseBody
    @GetMapping("/books/tag/{tagId}")
    public BooksPageDto getRecentNextPage(@PathVariable(value = "tagId", required = false) Integer tagId
            ,@RequestParam(value ="offset", required = false) Integer offset
            , @RequestParam(value ="limit", required = false) Integer limit){
        List<Book> list = bookService.getPageOfTagBooks(tagId, offset, limit);
        return new BooksPageDto(list);
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse> handleAddDeposit(@RequestParam(value = "sum", required = false) String sum) throws PayException {

        var paymentUrl="";
        UserEntity user = userService.getCurrentUser();

        if (user != null && Float.parseFloat(sum)>0) {
            try {
                paymentUrl = paymentServise.getPaymentUrl(sum, user.getHash());
            } catch (NoSuchAlgorithmException e) {
                throw new PayException("Проблема с оплатой " + e.getMessage());
            }
        }
        var response = new ApiResponse();
        response.setMessage(paymentUrl);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/buy")
    public String handleBuyBooks(
            @RequestParam(value = "sum") String sum
            ,@RequestParam(value = "user") String userHash) {

        UserEntity user = userService.getUserByHash(userHash);
        if (user != null) {

//            var costOfBooks = Float.parseFloat(sum);

            List<Book> booksFromRepo = book2UserService.getBooksFromRepoByTypeCodeAndUser("CART",user);

            for (Book book : booksFromRepo) {
                boolean isBuy = book2UserService.update("PAID", book, user);
                if (isBuy) {
                    bookService.decreaseCart(book.getId());
                    bookService.increasePaid(book.getId());
                    userService.changeUserBalanceBuy(user, book);
                }
            }

            return "redirect:/books/my";
        }
        return "redirect:/books/cart";
    }

}
