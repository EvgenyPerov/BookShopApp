package com.example.MyBookShopApp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class MyBookShopAppApplicationTests {

	@Autowired
	private MyBookShopAppApplication appApplication;

	@Value("${auth.secret}")
	private String secret;

	@Test
	@DisplayName("Это тест показывает что приложение не равно Null")
	void contextLoads() {
//		assertNotNull(appApplication);
		Assertions.assertNotNull(appApplication);
	}

	@Test
	@DisplayName("Значение secret = 'skillbox'")
	void matchSecretValue() {
		Assertions.assertEquals(secret,"skillbox");
		Assertions.assertTrue(secret.contains("box"));
	}

}
