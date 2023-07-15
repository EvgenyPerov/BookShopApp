package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.data.services.UserService;
import com.example.MyBookShopApp.security.RegistrationForm;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("/application-test.properties")
class UserRepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Добавить в БД нового юзера")
    void addNewUserTest() {
        UserEntity user = new UserEntity();
        user.setEmail("testMail@mail.ru");
        user.setName("testName");
        user.setPhone("89109998877");
        user.setPassword("111111");
        user.setHash("Hash111111");
        user.setRegTime(LocalDateTime.now());

        Assertions.assertNotNull(userRepository.save(user));

    }
}