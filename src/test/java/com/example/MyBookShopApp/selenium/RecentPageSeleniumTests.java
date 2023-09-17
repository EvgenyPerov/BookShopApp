package com.example.MyBookShopApp.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RecentPageSeleniumTests {

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
    @DisplayName("Выбор даты в Новинках")
    public void recentPageChangeDateTest() throws InterruptedException {
        String yearFrom = "2023", monthFrom = "08", dayFrom = "17";
        String yearTo = "2023", monthTo = "09", dayTo = "17";

        RecentPage recentPage = new RecentPage(driver);
        recentPage
                .callPage()
                .pause()
                .pause()
                .clickDateFrom()
                .pause()
                .pause()
                .pause()
                .clickDateMonthsFrom()
                .pause()
                .clickDateYearFrom()
                .pause()
                .setDateYearFrom(yearFrom)
                .pause()
                .setDateMonthFrom(monthFrom)
                .pause()
                .setDateDayFrom(dayFrom)
                .pause()
                .clickDateTo()
                .pause()
                .clickDateMonthsTo()
                .pause()
                .clickDateYearTo()
                .pause()
                .setDateYearTo(yearTo)
                .pause()
                .setDateMonthTo(monthTo)
                .pause()
                .setDateDayTo(dayTo)
                .pause()
                .pause();
        Assertions.assertTrue(driver.getPageSource().contains("Born Romantic"));
    }

}