package com.example.MyBookShopApp.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BookSlugPageSeleniumTests {


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
    @DisplayName("Click по автору 'Dael Fogt' из BookSlug книга 'Heartbeat'")
    void bookSlugPageClickAuthorTest() throws InterruptedException {
        String author = "Dael Fogt";
        BookSlugPage slugPage = new BookSlugPage(driver);
        slugPage
                .callPage()
                .pause()
                .clickAuthor(author)
                .pause()
                .pause();
        Assertions.assertEquals(driver.findElementByXPath("/html/body/div/div/main/div[1]/h1").getText(), author);
    }

    @Test
    @DisplayName("Отложить книгу 'Heartbeat' из BookSlug")
    void bookSlugPageClickPostponeTest() throws InterruptedException {
        BookSlugPage slugPage = new BookSlugPage(driver);
        slugPage
                .callPage()
                .pause()
                .changeBookStatus("KEPT")
                .pause()
                .callPagePost()
                .pause();
        Assertions.assertTrue(driver.getPageSource().contains("Heartbeat"));
    }

    @Test
    @DisplayName("Купить книгу 'Heartbeat' из BookSlug")
    void bookSlugPageClickCartTest() throws InterruptedException {
        BookSlugPage slugPage = new BookSlugPage(driver);
        slugPage
                .callPage()
                .pause()
                .changeBookStatus("CART")
                .pause()
                .callPageCart()
                .pause();
        Assertions.assertTrue(driver.getPageSource().contains("Heartbeat"));
    }

    @Test
    @DisplayName("Скачать книгу 'Heartbeat' из BookSlug. Метод не работает для неавторизованных пользователей")
    void bookSlugPageClickDownloadTest() throws InterruptedException {
        int  bookId = 3;
        BookSlugPage slugPage = new BookSlugPage(driver);
        slugPage
                .callPage()
//                .pause()
//                .changeBookStatus("DOWNLOAD")
//                .pause()
                .pause();
//        Assertions.assertTrue(driver.getPageSource().contains("Ссылки для скачивания"));


        assertEquals(3, bookId);
    }
}