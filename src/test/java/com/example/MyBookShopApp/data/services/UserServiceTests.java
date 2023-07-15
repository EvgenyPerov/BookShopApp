package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repo.BookReviewLikeRepository;
import com.example.MyBookShopApp.data.repo.BookReviewRepository;
import com.example.MyBookShopApp.data.repo.UserRepository;
import com.example.MyBookShopApp.security.*;
import com.example.MyBookShopApp.security.jwt.JwtUtil;
import com.example.MyBookShopApp.struct.book.review.BookReviewEntity;
import com.example.MyBookShopApp.struct.book.review.BookReviewLikeEntity;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@TestPropertySource("/application.properties")
class UserServiceTests {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private RegistrationForm registrationForm;

    @Autowired
    private BookReviewRepository bookReviewRepository;

    @Autowired
    private BookReviewLikeRepository bookReviewLikeRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @MockBean
    private UserRepository userRepositoryMock;

    @BeforeEach
    void setUp() {
        registrationForm = new RegistrationForm();
        registrationForm.setEmail("testMail@mail.ru");
        registrationForm.setName("testName");
        registrationForm.setPhone("89109998877");
        registrationForm.setPass("111111");
    }

    @AfterEach
    void tearDown() {
        registrationForm = null;
    }

    @Test
    @DisplayName("Регистрация нового юзера")
    void addUser() {
        UserEntity user = userService.addUser(registrationForm);
        Assertions.assertNotNull(user);

        Assertions.assertTrue(passwordEncoder.matches(registrationForm.getPass(),user.getPassword()));
        Assertions.assertTrue(CoreMatchers.is(registrationForm.getEmail()).matches(user.getEmail()));
        Assertions.assertTrue(Matchers.is(registrationForm.getPhone()).matches(user.getPhone()));
        Assertions.assertEquals(registrationForm.getName(), user.getName());

        Mockito.verify(userRepositoryMock, Mockito.times(1))
                .save(Mockito.any(UserEntity.class));
    }

    @Test
    void addUserFail() {
        Mockito.doReturn(new UserEntity())
                .when(userRepositoryMock)
                .findUserEntityByEmail(registrationForm.getEmail());

        UserEntity user = userService.addUser(registrationForm);
        Assertions.assertNull(user);
    }

    @Test
    @DisplayName("Создание валидного токена при авторизации")
    void createValidToken(){
        UserEntity user = userService.addUser(registrationForm);

        BookstoreUserDetails userDetails = new BookstoreUserDetails(user);
        String jwtToken = jwtUtil.generateToken(userDetails);

        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult(jwtToken);

        String wrongToken="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0TWFpbEBtYWlsLnJ1IiwiZXhwIjoxNjkwMTE3MTU1LCJpYXQiOjE2ODc1MjUxNTV9.bzoKPGc4CjL7D94OZpeyG9H2nrobtpEhdbSmGjWNFjz";
        String validToken = response.getResult();

        Assertions.assertTrue(jwtUtil.validateToken(validToken, userDetails));
        System.out.println(response.getResult());
        Assertions.assertNotNull(response.getResult());
    }

    @Test
    @DisplayName("Проверка авторизации не существующего User")
    void authOk(){

        ContactConfirmationPayload payload = new ContactConfirmationPayload();
        payload.setContact("Non ExistUser@mail.com");

        UserEntity user = userRepository.findUserEntityByEmail(payload.getContact());

        Assertions.assertNull(user);
    }

    @Test
    @DisplayName("Поставить Like и Dislike на отзыв для книги")
//    @WithUserDetails("My@mail.com")
//    @Transactional
    void setReviewLikeForBookTest() {
        UserEntity user = userService.getUserByName("Вася");
        Integer reviewId = 1;
        short value = 1;

        BookReviewEntity review = bookReviewRepository.findByIdIs(reviewId);

        List<Integer> list =userService.getReviewLikeRatingByReviewId(reviewId);
        int countLikeStart = list.get(1);
        int countDislikeStart = list.get(2);

        BookReviewLikeEntity bookReviewLike = BookReviewLikeEntity.builder()
                .review(review)
                .user(user)
                .time(LocalDateTime.now())
                .value(value)
                .build();
        bookReviewLikeRepository.save(bookReviewLike);

        list =userService.getReviewLikeRatingByReviewId(reviewId);
        int countLikeEnd = list.get(1);

        Assertions.assertNotEquals(countLikeStart, countLikeEnd);

        value = -1;
        bookReviewLike = BookReviewLikeEntity.builder()
                .review(review)
                .user(user)
                .time(LocalDateTime.now())
                .value(value)
                .build();
        bookReviewLikeRepository.save(bookReviewLike);

        list =userService.getReviewLikeRatingByReviewId(reviewId);
        int countDislikeEnd = list.get(2);

        Assertions.assertNotEquals(countDislikeStart, countDislikeEnd);
    }

    @Test
    @DisplayName("Получить рейтинг книги с учетом лайков на нее")
    void getReviewLikeRatingByReviewIdTest() {
        Integer reviewId = 1;

        List<Integer> list = userService.getReviewLikeRatingByReviewId(reviewId);

        int countLike = list.get(1);
        int countDislike = list.get(2);
        int ratingActual = list.get(0);

        int dev = (countDislike == 0) ? countLike / 1 : countLike / countDislike;
        int ratingExpected = 0;
        if (dev >= 9) ratingExpected = 5;  else
        if (dev >= 7) ratingExpected = 4;  else
        if (dev >= 5) ratingExpected = 3;  else
        if (dev >= 3) ratingExpected = 2;  else
        if (dev >= 1) ratingExpected = 1;

        Assertions.assertEquals(ratingActual, ratingExpected);
    }

}