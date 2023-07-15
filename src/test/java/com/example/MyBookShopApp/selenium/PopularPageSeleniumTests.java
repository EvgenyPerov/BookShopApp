package com.example.MyBookShopApp.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class PopularPageSeleniumTests {


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
    @DisplayName("Click по книге 'Heartbeat' из Популярного")
    public void popularPageTest() throws InterruptedException {
        PopularPage popularPage = new PopularPage(driver);
        popularPage
                .callPage()
                .pause()
                .clickBook()
                .pause()
                .pause();
        Assertions.assertTrue(driver.findElementByXPath("/html/body/div/div/main/div/div[1]/div[2]/div[1]/h1").getText().equals("Heartbeat"));
    }

}