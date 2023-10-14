package com.example.MyBookShopApp.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AuthorPageSeleniumTests {


    private static ChromeDriver driver;

    @BeforeAll
    static void setup(){
        System.setProperty("webdriver.chrome.driver", "C:/Users/1/Downloads/chromedriver_win32 (1)/chromedriver.exe");
        System.setProperty("webdriver.http.factory", "jdk-http-client");

        driver = new ChromeDriver();
        driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
    }

    @AfterAll
    static void teardown(){
        driver.quit();
    }

    @Test
    @DisplayName("Click по автору 'Ches Squire' из Авторов")
    void authorPageClickAuthorTest() throws InterruptedException {
        String author = "Ches Squire";
        AuthorPage authorPage = new AuthorPage(driver);
        authorPage
                .callPage()
                .pause()
                .clickAuthor(author)
                .pause()
                .pause();
        Assertions.assertEquals(driver.findElementByXPath("/html/body/div/div/main/div[1]/h1").getText(), author);
    }

}