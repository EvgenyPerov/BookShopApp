package com.example.MyBookShopApp.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

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
    @DisplayName("Click по автору 'Ches Squire' из BookSlug книга 'Heartbeat'")
    public void bookSlugPageClickAuthorTest() throws InterruptedException {
        String author = "Ches Squire";
        BookSlugPage slugPage = new BookSlugPage(driver);
        slugPage
                .callPage()
                .pause()
                .clickAuthor(author)
                .pause()
                .pause();
        Assertions.assertTrue(driver.findElementByXPath("/html/body/div/div/main/div[1]/h1").getText().equals(author));
    }

    @Test
    @DisplayName("Отложить книгу 'Heartbeat' из BookSlug")
    public void bookSlugPageClickPostponeTest() throws InterruptedException {
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
    public void bookSlugPageClickCartTest() throws InterruptedException {
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
    @DisplayName("Скачать книгу 'Heartbeat' из BookSlug")
    public void bookSlugPageClickDownloadTest() throws InterruptedException {
        BookSlugPage slugPage = new BookSlugPage(driver);
        slugPage
                .callPage()
                .pause()
                .changeBookStatus("DOWNLOAD")
                .pause()
                .pause();
        Assertions.assertTrue(driver.getPageSource().contains("Ссылки для скачивания"));
    }
}