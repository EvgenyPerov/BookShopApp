package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repo.*;
import com.example.MyBookShopApp.errs.myJwtException;
import com.example.MyBookShopApp.security.*;
import com.example.MyBookShopApp.security.jwt.JwtUtil;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.review.BookRatingEntity;
import com.example.MyBookShopApp.struct.book.review.BookReviewEntity;
import com.example.MyBookShopApp.struct.book.review.BookReviewLikeEntity;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final BookRatingRepository bookRatingRepository;

    private final BookReviewRepository bookReviewRepository;

    private final BookReviewLikeRepository bookReviewLikeRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final BookstoreUserDetailsService userDetailsService;

    private final JwtUtil jwtUtil;

    @Autowired
    private BookstoreUserDetailsService bookstoreUserDetailsService;

    @Value("${auth.expiredMessage}")
    private String expiredMessage;

    @Autowired
    public UserService(UserRepository userRepository, BookRatingRepository bookRatingRepository, BookReviewRepository bookReviewRepository, BookReviewLikeRepository bookReviewLikeRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, BookstoreUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.bookRatingRepository = bookRatingRepository;
        this.bookReviewRepository = bookReviewRepository;
        this.bookReviewLikeRepository = bookReviewLikeRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    public UserEntity getUserByHash(String hash) {
        return userRepository.findByHashIs(hash);
    }

    public boolean setRatingForBook(Book book, UserEntity user, int value) {

        if (book != null && user != null
                && value >= 0 && value <= 5) {
            BookRatingEntity bookRatingFromRepo = bookRatingRepository.findFirstByBookIdAndUserId(book.getId(), user.getId());

            if (bookRatingFromRepo == null) {
                bookRatingFromRepo = BookRatingEntity.builder()
                        .book(book)
                        .user(user)
                        .time(LocalDateTime.now())
                        .value(value)
                        .build();
            } else {
                bookRatingFromRepo.setTime(LocalDateTime.now());
                bookRatingFromRepo.setValue(value);
            }
            bookRatingRepository.save(bookRatingFromRepo);
            return true;
        }
        return false;
    }

    public Map<Integer, Integer> getRatingMapByBookId(Integer id) {
        List<BookRatingEntity> list = bookRatingRepository.findAllByBookId(id);
        float sumOfRating = 0f;
        int count = 0;

        Map<Integer, Integer> ratingMap = new HashMap<>();
        ratingMap.put(1, 0);
        ratingMap.put(2, 0);
        ratingMap.put(3, 0);
        ratingMap.put(4, 0);
        ratingMap.put(5, 0);

        ratingMap.put(100, 0);

        ratingMap.put(200, 0);

        if (!list.isEmpty()) {
            for (BookRatingEntity rating : list) {
                Integer key = rating.getValue();
                ratingMap.put(key, ratingMap.get(key) + 1);
                sumOfRating += key;
                count++;
            }
            int averageRating = Math.round(sumOfRating / count);
            ratingMap.put(100, averageRating);
            ratingMap.put(200, count);
        }
        return ratingMap;
    }

    public Map<BookReviewEntity, List<Integer>> getReviewMapByBookId(Integer id) {
        Map<BookReviewEntity, List<Integer>> reviewLikesMap = new HashMap<>();
        Map<BookReviewEntity, List<Integer>> resultMap = new LinkedHashMap<>();
        Map<Map<BookReviewEntity, List<Integer>>, Integer> noSortedMap = new HashMap<>();
        List<BookReviewEntity> list = bookReviewRepository.findAllByBookIdOrderByTimeDesc(id);

        for (BookReviewEntity reviewEntity : list) {
            List<Integer> likes = getReviewLikeRatingByReviewId(reviewEntity.getId());
            reviewLikesMap.put(reviewEntity, likes);
        }

        // составим новую сложную карту, где value = рейтинг лайков
        for (Map.Entry<BookReviewEntity, List<Integer>> pair : reviewLikesMap.entrySet()) {
            Map<BookReviewEntity, List<Integer>> map = new HashMap<>();
            BookReviewEntity key = pair.getKey();
            List<Integer> value = pair.getValue();
            map.put(key, value);
            noSortedMap.put(map,value.get(0));
        }

        // сортировка MAP по значению по убыванию "-v.getValue()"
        Map<Map<BookReviewEntity, List<Integer>>, Integer> sortedMap = noSortedMap.entrySet().stream()
                .sorted(Comparator.comparingInt(v -> -v.getValue()))
                .collect(Collectors.toMap(
                Map.Entry :: getKey,
                Map.Entry :: getValue,
                        (a,b) -> {throw new AssertionError();},
                        LinkedHashMap :: new
                ));

        sortedMap.keySet().forEach(k -> resultMap.putAll(k));

        return resultMap;
    }

    public boolean setReviewLikeForBook(Integer reviewId, UserEntity user, short value) {

        if (user != null && value != 0) {
            BookReviewEntity review = bookReviewRepository.findByIdIs(reviewId);

            BookReviewLikeEntity bookReviewLike = bookReviewLikeRepository.findFirstByReviewIdAndUser(reviewId, user);

            if (bookReviewLike == null) {
                bookReviewLike = BookReviewLikeEntity.builder()
                        .review(review)
                        .user(user)
                        .time(LocalDateTime.now())
                        .value(value)
                        .build();
            } else {
                bookReviewLike.setTime(LocalDateTime.now());
                bookReviewLike.setValue(value);
            }
            bookReviewLikeRepository.save(bookReviewLike);
            return true;
        }
        return false;
    }

    public Integer getBookByReviewId(Integer reviewId) {
        return bookReviewRepository.findByIdIs(reviewId).getBook().getId();
    }

    public List<Integer> getReviewLikeRatingByReviewId(Integer reviewId) {
        List<BookReviewLikeEntity> list = bookReviewLikeRepository.findAllByReviewId(reviewId);
        int countLike = 0;
        int countDislike = 0;

        for (BookReviewLikeEntity like : list) {
            if (like.getValue() > 0) ++countLike;
            else ++countDislike;
        }
        if (countDislike > countLike) return Arrays.asList(0, countLike, countDislike);

        int dev = (countDislike == 0) ? countLike / 1 : countLike / countDislike;

        if (dev >= 9) return Arrays.asList(5, countLike, countDislike);
        if (dev >= 7) return Arrays.asList(4, countLike, countDislike);
        if (dev >= 5) return Arrays.asList(3, countLike, countDislike);
        if (dev >= 3) return Arrays.asList(2, countLike, countDislike);
        if (dev >= 1) return Arrays.asList(1, countLike, countDislike);

        return Arrays.asList(0, countLike, countDislike);
    }

    public void addReviewForBook(Book book, UserEntity user, String text) {
        if (user != null && !text.isBlank()) {

            BookReviewEntity review = BookReviewEntity.builder()
                    .book(book)
                    .user(user)
                    .time(LocalDateTime.now())
                    .text(text)
                    .build();
            bookReviewRepository.save(review);
        }
    }

    public void addUser(RegistrationForm form) {
        if (userRepository.findUserEntityByEmail(form.getEmail()) == null) {
            UserEntity user = new UserEntity();
            user.setName(form.getName());
            user.setEmail(form.getEmail());
            user.setPhone(form.getPhone());
            user.setPassword(passwordEncoder.encode(form.getPass()));
            user.setRegTime(LocalDateTime.now());
            user.setHash(String.valueOf(this.hashCode()));
            user.setBalance(0);

            userRepository.save(user);
        }
    }

    public ContactConfirmationResponse login(ContactConfirmationPayload payload) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(payload.getContact(), payload.getCode()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult("true");
        return response;
    }

    public ContactConfirmationResponse jwtLogin(ContactConfirmationPayload payload) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(payload.getContact(), payload.getCode()));

        BookstoreUserDetails userDetails = (BookstoreUserDetails) userDetailsService.loadUserByUsername(payload.getContact());

        String jwtToken = jwtUtil.generateToken(userDetails);

        ContactConfirmationResponse response = new ContactConfirmationResponse();
        response.setResult(jwtToken);
        return response;
    }

    public UserEntity getCurrentUser()  {

        if (bookstoreUserDetailsService.getHandleTokenValid()) {

            Object user = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (user == "anonymousUser") {
                System.out.println("Авторизация не пройдена, User = " + user);
                return null;
            }

            if (!userHasJwtToken(user)) {
                return userRepository.findUserEntityByEmail(((CustomOAuth2User) user).getEmail());
            } else {
                return userRepository.findUserEntityByEmail(((BookstoreUserDetails) user).getUsername());
            }
        } else {
            System.out.println("<UserService>- " + expiredMessage);
            throw new myJwtException(expiredMessage);
//            throw new IllegalStateException(expiredMessage);
        }
    }

    public boolean userHasJwtToken(Object user){
         if (user instanceof UserDetails) {
            System.out.println("Тип = " + user.getClass().getSimpleName() + " Email = " + ((BookstoreUserDetails) user).getUsername());
            return true;
        }
        if (user instanceof OAuth2User) {
            System.out.println("Тип = " + user.getClass().getSimpleName() + " Email = " + ((CustomOAuth2User) user).getEmail());
        }
        return  false;
    }

}
