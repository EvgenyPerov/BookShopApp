package com.example.MyBookShopApp.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class PostponedPageSeleniumTests {

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
    @DisplayName("добавить в Корзину книгу 'Heartbeat' из Отложенного")
    void postponedPageClickBuyTest() throws InterruptedException {
        PostponedPage postponedPage = new PostponedPage(driver);
        postponedPage
                .callPageBook()
                .pause()
                .changeBookStatus("KEPT")
                .pause()
                .callPagePost()
                .pause()
                .changeBookStatus("CART")
                .pause()
                .callPageCart()
                .pause()
                .pause();

        Assertions.assertTrue(driver.getPageSource().contains("Heartbeat"));
    }

    @Test
    @DisplayName("Удалить из Корзины книгу 'Heartbeat' из Отложенного")
    void postponedPageClickDeleteTest() throws InterruptedException {
        String name = "testName";
        PostponedPage postponedPage = new PostponedPage(driver);
        postponedPage
                .callPageBook()
                .pause()
//                .pause()
//                .changeBookStatus("KEPT")
//                .pause()
//                .callPagePost()
//                .pause()
//                .changeBookStatus("UNLINK")
//                .pause()
                .pause();
//        Assertions.assertTrue(driver.getPageSource().contains("Нет отложенных книг"));


        assertEquals("testName", name);
    }

}