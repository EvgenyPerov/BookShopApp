package com.example.MyBookShopApp.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CartPageSeleniumTests {
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
    @DisplayName("Отложить книгу 'Heartbeat' из Корзины")
    public void cartPageClickPostponeTest() throws InterruptedException {
        CartPage cartPage = new CartPage(driver);
        cartPage
                .callPageBook()
                .pause()
                .changeBookStatus("CART")
                .pause()
                .callPageCart()
                .pause()
                .changeBookStatus("KEPT")
                .pause()
                .callPagePost()
                .pause()
                .pause();

        Assertions.assertTrue(driver.getPageSource().contains("Heartbeat"));
    }

    @Test
    @DisplayName("Удалить книгу 'Heartbeat' из Корзины")
    public void cartPageClickDeleteTest() throws InterruptedException {
        CartPage cartPage = new CartPage(driver);
        cartPage
                .callPageBook()
                .pause()
                .changeBookStatus("CART")
                .pause()
                .callPageCart()
                .pause()
                .changeBookStatus("UNLINK")
                .pause()
                .callPageCart()
                .pause()
                .pause();

        Assertions.assertTrue(driver.getPageSource().contains("Корзина пуста"));
    }

}