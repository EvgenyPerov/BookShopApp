package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.dto.ContactMessageDto;
import com.example.MyBookShopApp.data.dto.ReviewDto;
import com.example.MyBookShopApp.data.repo.*;
import com.example.MyBookShopApp.errs.myJwtException;
import com.example.MyBookShopApp.security.*;
import com.example.MyBookShopApp.security.jwt.JwtUtil;
import com.example.MyBookShopApp.struct.book.book.Book;
import com.example.MyBookShopApp.struct.book.review.BookRatingEntity;
import com.example.MyBookShopApp.struct.book.review.BookReviewEntity;
import com.example.MyBookShopApp.struct.book.review.BookReviewLikeEntity;
import com.example.MyBookShopApp.struct.book.review.MessageEntity;
import com.example.MyBookShopApp.struct.payments.BalanceTransactionEntity;
import com.example.MyBookShopApp.struct.user.Role;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
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

    private final TransactionRepository transactionRepository;

    @Autowired
    private BookstoreUserDetailsService bookstoreUserDetailsService;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private MessageRepository messageRepository;
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Value("${auth.expiredMessage}")
    private String expiredMessage;

    @Autowired
    public UserService(UserRepository userRepository, BookRatingRepository bookRatingRepository, BookReviewRepository bookReviewRepository, BookReviewLikeRepository bookReviewLikeRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, BookstoreUserDetailsService userDetailsService, JwtUtil jwtUtil, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.bookRatingRepository = bookRatingRepository;
        this.bookReviewRepository = bookReviewRepository;
        this.bookReviewLikeRepository = bookReviewLikeRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.transactionRepository = transactionRepository;
    }

    public UserEntity getUserByName(String name){
        return userRepository.findUserEntityByName(name);
    }

    public UserEntity getUserById(int id){
        return userRepository.findUserEntityById(id);
    }

    public UserEntity getUserByEmail(String email){
       return userRepository.findUserEntityByEmail(email);
    }

    public UserEntity getUserByPhone(String phone){
        return userRepository.findUserEntityByPhone(phone);
    }

    public UserEntity getUserByHash(String hash){ return userRepository.findByHashIs(hash);}

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
        var sumOfRating = 0f;
        var count = 0;

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
                if (key != 0) {
                    ratingMap.put(key, ratingMap.get(key) + 1);
                    sumOfRating += key;
                    count++;
                }
            }
            int averageRating = count==0? Math.round(sumOfRating ) : Math.round(sumOfRating / count);
            ratingMap.put(100, averageRating);
            ratingMap.put(200, count);
        }
        return ratingMap;
    }

    public Map<BookReviewEntity, List<Integer>> getReviewMapByBookId(Book book) {
        Map<BookReviewEntity, List<Integer>> reviewLikesMap = new HashMap<>();
        Map<BookReviewEntity, List<Integer>> resultMap = new LinkedHashMap<>();
        Map<Map<BookReviewEntity, List<Integer>>, Integer> noSortedMap = new HashMap<>();
        List<BookReviewEntity> list = bookReviewRepository.findAllByBookAndIsCheckedInOrderByTimeDesc(book, Arrays.asList(0, 1));

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

    public List<Integer> getReviewLikeRatingByReviewId(Integer reviewId) {
        List<BookReviewLikeEntity> list = bookReviewLikeRepository.findAllByReviewId(reviewId);
        var countLike = 0;
        var countDislike = 0;

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

    public boolean addReviewForBook(Book book, UserEntity user, String text) {
        if (user != null && !text.isBlank()) {
            BookReviewEntity review = BookReviewEntity.builder()
                    .book(book)
                    .user(user)
                    .time(LocalDateTime.now())
                    .text(text)
                    .build();
            bookReviewRepository.save(review);
            return true;
        }
        return false;
    }

    public UserEntity addUser(RegistrationForm form) {

        var userByEmail = userRepository.findUserEntityByEmail(form.getEmail());
        var userByPhone = userRepository.findUserEntityByPhone(form.getPhone());

        if (userByEmail == null && userByPhone == null) {
            var user = new UserEntity();
            user.setName(form.getName());
            user.setEmail(form.getEmail());
            user.setPhone(form.getPhone());
            user.setPassword(passwordEncoder.encode(form.getPass()));
            user.setRegTime(LocalDateTime.now());
            user.setHash(String.valueOf(this.hashCode()));
            user.setBalance(0);
            user.setStatus(1);

            Role newRole;
            Set<Role> roleSet = new HashSet<>();
            if (getCurrentUser() != null && getCurrentUser().getRoles().stream()
                    .anyMatch(role -> role.getRoleName().equals("ROLE_ADMIN")))  {
                newRole = roleRepository.findByRoleName("ROLE_ADMIN");
                roleSet.add(newRole);
                user.setRoles(roleSet);
            } else {
                newRole = roleRepository.findByRoleName("ROLE_USER");
                roleSet.add(newRole);
                user.setRoles(roleSet);
            }

            userRepository.save(user);
            return user;
        } else {
            return userByEmail;
        }
    }

    public void changeUserPassword(RegistrationForm form, UserEntity user) {

        if (user != null) {
            logger.info("Сервис смены пароля для " + user.getName() + " на " + form.getPass());
            user.setPassword(passwordEncoder.encode(form.getPass()));
            userRepository.save(user);
        }
    }

    public void changeUserPhone(RegistrationForm form) {
        UserEntity user = getCurrentUser();
        user.setPhone(form.getPhone());
        userRepository.save(user);
    }

    public void changeUserName(RegistrationForm form) {
        UserEntity user = getCurrentUser();
        user.setName(form.getName());
        userRepository.save(user);
    }

    public void changeUserBalanceDeposit(UserEntity user, float sum){
        user.setBalance(user.getBalance() + sum);
        userRepository.save(user);

        var transaction = new BalanceTransactionEntity();
            transaction.setUserId(user.getId());
            transaction.setValue(sum);
            transaction.setBookId(0);
            transaction.setDescription("Пополнение счета");
            transactionRepository.save(transaction);
    }


    public void changeUserBalanceBuy(UserEntity user, Book book){
        user.setBalance((float) (user.getBalance() - book.discountPrice()));
        userRepository.save(user);

        var transaction = new BalanceTransactionEntity();
            transaction.setUserId(user.getId());
            transaction.setValue((float) -book.discountPrice());
            transaction.setBookId(book.getId());
            transaction.setDescription("Покупка книги:" + book.getTitle());
            transactionRepository.save(transaction);
    }

    public Page<BalanceTransactionEntity> getPageOfBalanceTransactionDesc(UserEntity user, Integer offset, Integer limit, String sort){
        Pageable nextPage = PageRequest.of(offset, limit);

        return sort.equalsIgnoreCase("desc")?
                transactionRepository.findAllByUserIdOrderByTimeDesc(user.getId(), nextPage) :
                transactionRepository.findAllByUserIdOrderByTimeAsc(user.getId(), nextPage);
    }



    public ContactConfirmationResponse login(ContactConfirmationPayload payload) {
        var authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(payload.getContact(), payload.getCode()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        var response = new ContactConfirmationResponse();
        response.setResult("true");
        return response;
    }

    public ContactConfirmationResponse jwtLogin(ContactConfirmationPayload payload) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(payload.getContact(), payload.getCode()));

        logger.info("payload.getContact() - " + payload.getContact());
        logger.info("payload.getCode() - "+ payload.getCode());

        BookstoreUserDetails userDetails = (BookstoreUserDetails) userDetailsService.loadUserByUsername(payload.getContact());

        String jwtToken = jwtUtil.generateToken(userDetails);

        var response = new ContactConfirmationResponse();
        response.setResult(jwtToken);

        return response;
    }


    public ContactConfirmationResponse jwtLoginByPhoneNumber(ContactConfirmationPayload payload) {
        var registrationForm = new RegistrationForm();
        registrationForm.setPhone(payload.getContact());
        registrationForm.setPass(payload.getCode());

        addUser(registrationForm);

        var userDetails = userDetailsService.loadUserByUsername(payload.getContact());
        String jwtToken = jwtUtil.generateToken(userDetails);
        var response = new ContactConfirmationResponse();
        response.setResult(jwtToken);

        return response;
    }

    public boolean userHasJwtToken(Object user){
        if (user instanceof UserDetails) {
            logger.info("Тип входа = токен " + user.getClass().getSimpleName() + " Email or Phone = " + ((BookstoreUserDetails) user).getUsername());
            return true;
        }
        if (user instanceof OAuth2User) {
            logger.info("Тип входа = OAuth2 " + user.getClass().getSimpleName() + " Email = " + ((CustomOAuth2User) user).getEmail());
        }
        return  false;
    }

    public UserEntity getCurrentUser()  {

        if (bookstoreUserDetailsService.getHandleTokenValid()) {

            Object authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return null;
            }

            Object user = ((Authentication) authentication).getPrincipal();

            if (user == null || user == "anonymousUser") {
                logger.info("Авторизация не пройдена, User = " + user);
                return null;
            }

            if (!userHasJwtToken(user)) {
                return userRepository.findUserEntityByEmail(((CustomOAuth2User) user).getEmail());
            } else {
                String name = ((UserDetails) user).getUsername();
                if (name.contains("@")) {
                    logger.info("Регистрированный клиент по Email - " + name);
                    return userRepository.findUserEntityByEmail(name);
                } else {
                    logger.info("Регистрированный клиент по Phone - " + name);
                    return userRepository.findUserEntityByPhone(name);
                }
            }
        } else {
            throw new myJwtException(expiredMessage);
        }
    }

    public List<BookReviewEntity> getReviewBetweenDate(ReviewDto form){
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        LocalDateTime from;
        if (form.getStartDatetime() == null || form.getStartDatetime().isBlank()){
            from = LocalDateTime.now().minusHours(24);
        } else {
            from = LocalDateTime.parse(form.getStartDatetime(), formatter);
        }
//
        LocalDateTime to;
        if (form.getEndDatetime() == null || form.getEndDatetime().isBlank()){
            to = LocalDateTime.now();
        } else {
            to = LocalDateTime.parse(form.getEndDatetime(), formatter);
        }

        List<Integer> statuses = (form.getStatus() != null && form.getStatus().equals("checked"))? Arrays.asList(-1, 1) : Arrays.asList(0);

        return bookReviewRepository.findAllByIsCheckedInAndTimeBetweenOrderByTime(statuses, from, to);
    }

    public void changeStatusForReview(ReviewDto form){

        int isChecked = (form.getStatus().equals("approve"))? 1 : -1;

        BookReviewEntity review = bookReviewRepository.findByIdIs(form.getId());
        review.setIsChecked(isChecked);
        bookReviewRepository.save(review);
    }

    public void changeStatusForAllReviews(ReviewDto form){
        List<BookReviewEntity> reviews;
        int isChecked = (form.getStatus().equals("approve"))? 1 : -1;
        List<Integer> idListInteger = new ArrayList<>();

        form.getIds().forEach(idString -> idListInteger.add(Integer.parseInt(idString)));
        reviews = bookReviewRepository.findAllByIdIn(idListInteger);

        reviews.forEach(review -> review.setIsChecked(isChecked));

        bookReviewRepository.saveAll(reviews);
    }

    public UserEntity getUserByDataForm(RegistrationForm form){
        UserEntity user = null;
        if (form.getId() != 0) {
            user = getUserById(form.getId());
        } else if (form.getEmail() != null && !form.getEmail().isBlank()) {
            user = getUserByEmail(form.getEmail());
        } else if (form.getPhone() != null && !form.getPhone().isBlank()) {
            user = getUserByPhone(form.getPhone());
        } else if (form.getName() != null && !form.getName().isBlank()) {
            user = getUserByName(form.getName());
        }
        return user;
    }

    public int getCountReviewByUserAndStatus(UserEntity user){
        if (user == null) return 0;
        return bookReviewRepository.countByUserAndIsChecked(user, -1);
    }

    public void changeStatusUser(ReviewDto form) {
        int status = form.getStatus().equals("active")? 1 : 0;

        var user = userRepository.findUserEntityById(form.getId());
        if (user != null) {
            user.setStatus(status);
            userRepository.save(user);
        }
    }

    public void addMessageToSupport(ContactMessageDto form){
        UserEntity user = getCurrentUser();
        String name;
        String email;
        var userId = 0;

        if (user != null) {
            name = user.getName();
            email = user.getEmail();
            userId = user.getId();
        } else {
            name = form.getName();
            email = form.getEmail();
        }

        var message = new MessageEntity();
        message.setTime(LocalDateTime.now());
        message.setName(name);
        message.setEmail(email);
        message.setSubject(form.getSubject());
        message.setText(form.getText());
        message.setUserId(userId);
        messageRepository.save(message);
    }
}
