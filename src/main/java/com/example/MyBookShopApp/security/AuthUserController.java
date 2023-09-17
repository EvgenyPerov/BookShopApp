package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.data.dto.TransactionsPageDto;
import com.example.MyBookShopApp.data.services.Book2UserService;
import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.data.services.PaymentServise;
import com.example.MyBookShopApp.data.services.UserService;
import com.example.MyBookShopApp.errs.BadRequestException;
import com.example.MyBookShopApp.security.jwt.JwtService;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.payments.BalanceTransactionEntity;
import com.example.MyBookShopApp.struct.user.UserEntity;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Controller
public class AuthUserController {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Value("${appEmail.email}")
    private String email;

    private Boolean isChangeUserPhone = null;

    private Boolean isChangeUserName = null;

    private UserService userService;

    private final BookService bookService;

    private final JwtService jwtService;

    private final Book2UserService book2UserService;

    @Autowired
    private  SmsService smsService;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private PaymentServise paymentServise;

    @Autowired
    public AuthUserController(UserService userService, BookService bookService, JwtService jwtService, Book2UserService book2UserService) {
        this.userService = userService;
        this.bookService = bookService;
        this.jwtService = jwtService;
        this.book2UserService = book2UserService;
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

    @GetMapping("/signin")
    public String signInPage(Model model){
        System.out.println("Переход на страницу Вход");
        return "signin";
    }

    @GetMapping("/signup")
    public String signUpPage(Model model){
        System.out.println("Переход на страницу Регистрация");
        model.addAttribute("regForm", new RegistrationForm());
        return "signup";
    }

    @PostMapping("/requestContactConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestContactConfirmation(@RequestBody ContactConfirmationPayload payload) throws URISyntaxException, BadRequestException {
        System.out.println("Сработал мапинг /requestContactConfirmation");
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");

        if (!payload.getContact().contains("@")){
            String secret = smsService.sendSecretCodeSms(payload.getContact());
            smsService.saveSmsCode(new SmsCode(secret, 120)); // 120 секунд
        }

        return response;
    }

    @PostMapping("/requestEmailConfirmation")
    @ResponseBody
    public ContactConfirmationResponse handleRequestEmailConfirmation(@RequestBody ContactConfirmationPayload payload) throws URISyntaxException, BadRequestException {
        ContactConfirmationResponse response = new ContactConfirmationResponse();

        String secret = smsService.generateRandomSMSCode(4);

        SimpleMailMessage message = new SimpleMailMessage();

        System.out.println("from = " + email); //
        message.setFrom(email);

        System.out.println("to = " + payload.getContact()); //
        message.setTo(payload.getContact());

        message.setSubject("Bookshop app code confirmation");
        message.setText(secret);

        try {
            javaMailSender.send(message);
            smsService.saveSmsCode(new SmsCode(secret, 300)); // 5 минут
            response.setResult("true");
        } catch (MailException e) {
            logger.warning("Не удалось отправить сообщение с кодом. Ошибка: " + e);
        }

        return response;
    }

    @PostMapping("/approveContact")
    @ResponseBody
    public ContactConfirmationResponse handleApproveContact(@RequestBody ContactConfirmationPayload payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();

        if (smsService.verifySmsCode(payload.getCode())) {
            response.setResult("true");

                System.out.println("Код верный, выход выполнен"); //

        }
        else System.out.println("Код не верный, выход не выполнен"); //

        return response;
    }

    @PostMapping("/approveContactProfile")
    @ResponseBody
    public ContactConfirmationResponse handleApproveContactProfile(@RequestBody ContactConfirmationPayload payload) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();

        if (smsService.verifySmsCode(payload.getCode())) {
            response.setResult("true");
            isChangeUserPhone = true;
        }
        return response;
    }
 // удалить
    @PostMapping("/changeNameProfile")
    @ResponseBody
    public ContactConfirmationResponse handleChangeNameProfile(@RequestBody ProfileChangeName profileChangeName) {
        ContactConfirmationResponse response = new ContactConfirmationResponse();
        isChangeUserName= true;

        response.setResult("true");
        return response;
    }

    @PostMapping("/reg")
    public String signUpRegistrationPage(RegistrationForm form, Model model){
        userService.addUser(form);
        model.addAttribute("regOk", true);
        System.out.println("Пользователь добавлен в БД, регистрация прошла успешно");
        return "redirect:/signin";
    }

    @PostMapping("/login-by-email")
    @ResponseBody
    public ContactConfirmationResponse handleLogin(@RequestBody ContactConfirmationPayload payload,
                                                   HttpServletResponse httpServletResponse){
        System.out.println("@PostMapping(/login-by-email)"); //

        ContactConfirmationResponse loginResponse = userService.jwtLogin(payload);
        System.out.println("loginResponse = !!!"); //


        if (jwtService.isTokenInBlacklist(loginResponse.getResult())){
            System.out.println("Токен недействителен");
            return null;
        }

        Cookie cookie = new Cookie("token", loginResponse.getResult());
        httpServletResponse.addCookie(cookie);

        return loginResponse;
    }

    @PostMapping("/login-by-phone-number")
    @ResponseBody
    public ContactConfirmationResponse handleLoginByPhoneNumber(@RequestBody ContactConfirmationPayload payload,
                                                 HttpServletResponse response,
                                                 @CookieValue(name = "tryLoginPhone", required = false) String cookieLogin) {


        if (smsService.verifySmsCode(payload.getCode())) {
            ContactConfirmationResponse loginResponse = userService.jwtLoginByPhoneNumber(payload);

            if (jwtService.isTokenInBlacklist(loginResponse.getResult())) {
                System.out.println("Токен недействителен");
                return null;
            }

            Cookie cookie = new Cookie("token", loginResponse.getResult());
            response.addCookie(cookie);
            return loginResponse;

        } else {
                        return null;
        }
    }

    @GetMapping("/books/my")
    public String myPage(Model model,
                         @CookieValue(name = "cartContents", required = false) String cartContents,
                         @CookieValue(name = "postponedContents", required = false) String postponedContents){
        System.out.println("Переход на страницу My");


        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            model.addAttribute("curUser", user);
            List<Book> list = book2UserService.getPageOfBooksFromRepoByTypeCodeAndUser(0, 20,"PAID", user);
            model.addAttribute("myPaidBooks", list);

            // после авторизации все отложенные и корзины книги перезаписываются в базу данных к этому пользователю
            List<Book> booksPostponed = bookService.getBooksFromCookies(postponedContents);
                if (booksPostponed != null) {
                    for (Book book : booksPostponed) {
                        book2UserService.update("KEPT", book, user);
                    }
                }

            List<Book> booksCart = bookService.getBooksFromCookies(cartContents);
                if (booksCart != null) {
                    for (Book book : booksCart) {
                        book2UserService.update("CART", book, user);
                    }
                }
        }
        return "my";
    }

    @ApiOperation("operation to get my books by page")
    @GetMapping("/books/page/my")
    @ResponseBody
    public BooksPageDto myBooksPage(Model model,
                                    @RequestParam(value ="offset", required = false) Integer offset,
                                    @RequestParam(value ="limit", required = false) Integer limit){
        UserEntity user = userService.getCurrentUser();

        if (user != null) {
            model.addAttribute("curUser", user);

            List<Book> list = book2UserService.getPageOfBooksFromRepoByTypeCodeAndUser(offset, limit,"PAID", user);
            return new BooksPageDto(list);
        }
        return new BooksPageDto(new ArrayList<>());
    }

    @PostMapping("/changeBookStatus/ARCHIVED/{bookId}")
    public String handleChangeBookStatusCart(@PathVariable(value = "bookId", required = false) String bookId){
        System.out.println("Сработал контроллер /changeBookStatus/ARCHIVED/" + bookId);

        Book book = bookService.getBookById(Integer.parseInt(bookId));
        UserEntity user = userService.getCurrentUser();

        if (user != null) {
            boolean isChangeArchived = book2UserService.updateStatusOfBookPaidOrArchived(book, user);
        }
        return "redirect:/books/" + bookId;
    }

    @GetMapping("/my/archive")
    public String myPage(Model model) {
        System.out.println("Переход на страницу MyArchive");

        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            model.addAttribute("curUser", user);
            model.addAttribute("myArchihedBooks", book2UserService.getBooksFromRepoByTypeCodeAndUser("ARCHIVED", user));
        }

        return "myarchive";
    }

    @ApiOperation("operation to get looked books")
    @GetMapping("/books/looked")
    public String lookedBooks(Model model){
        System.out.println("Переход на страницу Просмотренное");
//        model.addAttribute("lookedBooks",bookService.getPageOfPopularBooks(0, 20).getContent());
        UserEntity user = userService.getCurrentUser();

        if (user != null) {
            model.addAttribute("curUser", user);
            model.addAttribute("lookedBooks",book2UserService.getPageOfLookedBooksByUserLastMonth(0, 20, user));
        }

        return "/books/look";
    }

    @ApiOperation("operation to get looked books by page")
    @GetMapping("/books/page/looked")
    @ResponseBody
    public BooksPageDto lookedBooksPage(Model model,
                                        @RequestParam(value ="offset", required = false) Integer offset
                                      , @RequestParam(value ="limit", required = false) Integer limit){
        UserEntity user = userService.getCurrentUser();

        if (user != null) {
            model.addAttribute("curUser", user);
            return new BooksPageDto(book2UserService.getPageOfLookedBooksByUserLastMonth(offset, limit, user));
        }

        return new BooksPageDto(new ArrayList<>());
    }

    @GetMapping("/profile")
    public String profilePage(Model model,
                              @RequestParam(value = "isChangeUserData", required = false) boolean isChangeUserData,
                              @RequestParam(value = "result", required = false) Boolean isPaymentOk,
                              @RequestParam(value = "error", required = false) String errorMessage,
                              @RequestParam(value = "InvId", required = false) String invId,
                              @RequestParam(value = "SignatureValue", required = false) String signatureValue,
                              @RequestParam(value = "OutSum", required = false) String outSum) throws NoSuchAlgorithmException, InterruptedException {
        System.out.println("Переход на страницу Profile");

        if (signatureValue != null) {
            String createSignatureValue = paymentServise.createSignatureValue(outSum, invId);

            float summ = Float.parseFloat(outSum);
            if (signatureValue.toUpperCase().equals(createSignatureValue) && summ>0) {
                isPaymentOk = true;
                UserEntity user = userService.getUserByHash(invId);
                userService.changeUserBalanceDeposit(user, summ);
            } else {
                isPaymentOk = false;
                errorMessage = "Пополнение не выполнено";
            }
            return "redirect:/profile";
        }

        model.addAttribute("curUser", userService.getCurrentUser());
        model.addAttribute("changeDataForm", new RegistrationForm());
        model.addAttribute("ChangeOk", isChangeUserData);
        model.addAttribute("PaymentOk", isPaymentOk);
        model.addAttribute("errorMessage", errorMessage);
        model.addAttribute("transactions", userService.getPageOfBalanceTransactionDesc(userService.getCurrentUser(),0,2 , "desc").getContent());


        return "profile";
    }

    @ApiOperation("этот метод мапинга нужен для JS файла, постраничная загрузка истории транзакций")
    @ResponseBody
    @GetMapping("/transactions")
    public TransactionsPageDto getTransactionsNextPage(
              @RequestParam(value ="sort", required = false) String sort
            , @RequestParam(value ="offset", required = false) Integer offset
            , @RequestParam(value ="limit", required = false) Integer limit) {
        System.out.println("Передача данных истории транзакций в JS с параметрами offset = "+ offset +
                " limit= "+ limit + " sort= "+ sort);

        List<BalanceTransactionEntity> list =
                userService.getPageOfBalanceTransactionDesc(userService.getCurrentUser(), offset, limit, sort).getContent();

        System.out.println("Список транзакций = " + list.size() + " пустой - "+ list.isEmpty());
        return new TransactionsPageDto(list);
    }

    @PostMapping("/profileChange")
    public String profilePageChangeData(RegistrationForm form){
        boolean isChangeUserData = false;

        if (form.getPass() != null && !form.getPass().isBlank() && form.getPass().equals(form.getPass2())) {
        userService.changeUserPassword(form);
            isChangeUserData = true;
            System.out.println("Изменение пароля пользователя выполнено успешно");
        }

        if (isChangeUserPhone != null && isChangeUserPhone) {
            isChangeUserPhone = false;
            String phone = userService.getCurrentUser().getPhone();
            if ((phone != null && !phone.equals(form.getPhone())) || phone == null)
            {
                userService.changeUserPhone(form);
                isChangeUserData = true;
                System.out.println("Изменение телефона пользователя выполнены успешно");
            }
        }

        if (isChangeUserName != null && isChangeUserName) {
            userService.changeUserName(form);
            isChangeUserData = true;
            isChangeUserName = false;
            System.out.println("Изменение имени пользователя выполнены успешно");
        }

        return "redirect:/profile?isChangeUserData=" + isChangeUserData;
    }

}
