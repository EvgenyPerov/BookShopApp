package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.dto.BooksPageDto;
import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.data.dto.TransactionsPageDto;
import com.example.MyBookShopApp.data.responses.ResponseResultOrError;
import com.example.MyBookShopApp.data.services.Book2UserService;
import com.example.MyBookShopApp.data.services.BookService;
import com.example.MyBookShopApp.data.services.PaymentServise;
import com.example.MyBookShopApp.data.services.UserService;
import com.example.MyBookShopApp.security.jwt.JwtService;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.payments.BalanceTransactionEntity;
import com.example.MyBookShopApp.struct.user.Role;
import com.example.MyBookShopApp.struct.user.UserEntity;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Controller
public class AuthUserController {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private static final String CUR_USER = "curUser";

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
        logger.info("Переход на страницу Вход");
        return "signin";
    }

    @GetMapping("/signup")
    public String signUpPage(Model model){
        logger.info("Переход на страницу Регистрация пользователя");
        model.addAttribute("regForm", new RegistrationForm());
        return "signup";
    }

    @PostMapping("/requestContactConfirmation")
    @ResponseBody
    public ResponseResultOrError handleRequestContactConfirmation(@RequestBody ContactConfirmationPayload payload) {
        var response = new ResponseResultOrError();
        response.setError("Error phone");
        response.setResult(true);

        if (!payload.getContact().contains("@")){
            String secret = smsService.sendSecretCodeSms(payload.getContact());
            smsService.saveSmsCode(new SmsCode(secret, 120)); // 120 секунд
            response.setResult(true);
        }

        return response;
    }

    @PostMapping("/registrationEmailConfirmation")
    @ResponseBody
    public ResponseResultOrError handleRequestEmailConfirmation(@RequestBody ContactConfirmationPayload payload)  {
        var response = new ResponseResultOrError();
        response.setError("Error email");
        String secret = smsService.generateRandomSMSCode(4);

        var message = new SimpleMailMessage();

        message.setFrom(email);

        message.setTo(payload.getContact());

        message.setSubject("Bookshop app code confirmation");
        message.setText(secret);

        try {
            javaMailSender.send(message);
            smsService.saveSmsCode(new SmsCode(secret, 300)); // 5 минут
            response.setResult(true);
        } catch (MailException e) {
            logger.warning("Не удалось отправить сообщение с кодом. Ошибка: " + e);
        }

        return response;
    }

    @PostMapping("/approveContact")
    @ResponseBody
    public ResponseResultOrError handleApproveContact(@RequestBody ContactConfirmationPayload payload) {
        var response = new ResponseResultOrError();

        if (smsService.verifySmsCode(payload.getCode())) {
            response.setResult(true);
            response.setError("OK");
        }
        else {
            response.setResult(false);
            response.setError("Неверный код или срок ожидания превышен. ");
        }
        return response;
    }

    @PostMapping("/approveContactProfile")
    @ResponseBody
    public ResponseResultOrError handleApproveContactProfile(@RequestBody ContactConfirmationPayload payload) {
        var response = new ResponseResultOrError();

        if (smsService.verifySmsCode(payload.getCode())) {
            response.setResult(true);
            response.setError("OK");
            isChangeUserPhone = true;
        }else {
            response.setResult(false);
            response.setError("Wrong phone");
        }
        return response;
    }

    @PostMapping("/changeNameProfile")
    @ResponseBody
    public ResponseResultOrError handleChangeNameProfile() {
        var response = new ResponseResultOrError();
        isChangeUserName= true;
        response.setResult(true);
        response.setError("OK");
        return response;
    }

    @PostMapping("/reg")
    public String signUpRegistrationPage(@Valid RegistrationForm form, Model model){
        UserEntity user = userService.addUser(form);
        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> role.getRoleName().equals("ROLE_ADMIN"));

        model.addAttribute("regOk", true);

        return isAdmin? "redirect:/admin/registration" : "redirect:/signin";
    }

    @PostMapping("/login-by-email")
    @ResponseBody
    public ResponseResultOrError handleLogin(@RequestBody ContactConfirmationPayload payload,
                                                   HttpServletResponse httpServletResponse){
        var response = new ResponseResultOrError();

        ContactConfirmationResponse loginResponse = userService.jwtLogin(payload);

        if (loginResponse != null) {
            if (jwtService.isTokenInBlacklist(loginResponse.getResult())) {
                logger.info("Токен недействителен");
                response.setResult(false);
                response.setError("Wrong number");
                return response;
            }

            var cookie = new Cookie("token", loginResponse.getResult());
            httpServletResponse.addCookie(cookie);

            response.setResult(true);

            Optional<Role> role = userService.getUserByEmail(payload.getContact()).getRoles().stream().findFirst();
            if (role.isPresent())  response.setError(role.get().getRoleName());
        }

        return response;
    }

    @PostMapping("/login-by-phone-number")
    @ResponseBody
    public ResponseResultOrError handleLoginByPhoneNumber(@RequestBody ContactConfirmationPayload payload,
                                                 HttpServletResponse httpServletResponse) {

        var response = new ResponseResultOrError();
        response.setError("Wrong number");

        if (smsService.verifySmsCode(payload.getCode())) {
            ContactConfirmationResponse loginResponse = userService.jwtLoginByPhoneNumber(payload);

            if (jwtService.isTokenInBlacklist(loginResponse.getResult())) {
                logger.info("Токен недействителен");
                response.setResult(false);
                return response;
            }

            var cookie = new Cookie("token", loginResponse.getResult());
            httpServletResponse.addCookie(cookie);
            response.setResult(true);

            Optional<Role> role = userService.getUserByPhone(payload.getContact()).getRoles().stream().findFirst();
            if (role.isPresent())  response.setError(role.get().getRoleName());

        }
        return response;
    }

    @GetMapping("/books/my")
    public String myPage(Model model,
                         @CookieValue(name = "cartContents", required = false) String cartContents,
                         @CookieValue(name = "postponedContents", required = false) String postponedContents){
        logger.info("Переход на страницу My");


        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            model.addAttribute(CUR_USER, user);
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
            model.addAttribute(CUR_USER, user);

            List<Book> list = book2UserService.getPageOfBooksFromRepoByTypeCodeAndUser(offset, limit,"PAID", user);
            return new BooksPageDto(list);
        }
        return new BooksPageDto(new ArrayList<>());
    }

    @PostMapping("/changeBookStatus/ARCHIVED/{bookId}")
    public String handleChangeBookStatusCart(@PathVariable(value = "bookId", required = false) String bookId){
        var book = bookService.getBookById(Integer.parseInt(bookId));
        UserEntity user = userService.getCurrentUser();

        if (user != null) {
            book2UserService.updateStatusOfBookPaidOrArchived(book, user);
        }
        return "redirect:/books/" + bookId;
    }

    @GetMapping("/my/archive")
    public String myPage(Model model) {
        logger.info("Переход на страницу MyArchive");

        UserEntity user = userService.getCurrentUser();
        if (user != null) {
            model.addAttribute(CUR_USER, user);
            model.addAttribute("myArchihedBooks", book2UserService.getBooksFromRepoByTypeCodeAndUser("ARCHIVED", user));
        }

        return "myarchive";
    }

    @ApiOperation("operation to get looked books")
    @GetMapping("/books/looked")
    public String lookedBooks(Model model){
        logger.info("Переход на страницу Просмотренное");
        UserEntity user = userService.getCurrentUser();

        if (user != null) {
            model.addAttribute(CUR_USER, user);
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
            model.addAttribute(CUR_USER, user);
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
                              @RequestParam(value = "OutSum", required = false) String outSum) throws NoSuchAlgorithmException {
        logger.info("Переход на страницу Profile");

        if (signatureValue != null) {
            String createSignatureValue = paymentServise.createSignatureValue(outSum, invId);

            var summ = Float.parseFloat(outSum);
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

        model.addAttribute(CUR_USER, userService.getCurrentUser());
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

        List<BalanceTransactionEntity> list =
                userService.getPageOfBalanceTransactionDesc(userService.getCurrentUser(), offset, limit, sort).getContent();

        return new TransactionsPageDto(list);
    }

    @PostMapping("/profileChange")
    public ModelAndView profilePageChangeData(RegistrationForm form){
        var isChangeUserData = false;

        if (form.getPass() != null && !form.getPass().isBlank() && form.getPass().equals(form.getPass2())) {
            UserEntity user = userService.getCurrentUser();
            userService.changeUserPassword(form, user);
            isChangeUserData = true;
            logger.info("Изменение пароля пользователя выполнено успешно");
        }

        if (isChangeUserPhone != null && isChangeUserPhone) {
            isChangeUserPhone = false;
            String phone = userService.getCurrentUser().getPhone();
            if ((phone != null && !phone.equals(form.getPhone())) || phone == null)
            {
                userService.changeUserPhone(form);
                isChangeUserData = true;
                logger.info("Изменение телефона пользователя выполнены успешно");
            }
        }

        if (isChangeUserName != null && isChangeUserName) {
            userService.changeUserName(form);
            isChangeUserData = true;
            isChangeUserName = false;
            logger.info("Изменение имени пользователя выполнены успешно");
        }
        var mav = new ModelAndView();
        mav.setStatus(HttpStatus.OK);
        mav.setViewName("profile");
        mav.addObject(CUR_USER, userService.getCurrentUser());
        mav.addObject("changeDataForm", new RegistrationForm());
        mav.addObject("ChangeOk", isChangeUserData);
        mav.addObject("transactions", userService.getPageOfBalanceTransactionDesc(userService.getCurrentUser(),0,2 , "desc").getContent());

        return mav;
    }

}
