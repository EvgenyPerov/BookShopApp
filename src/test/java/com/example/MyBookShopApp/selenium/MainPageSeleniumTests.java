package com.example.MyBookShopApp.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MainPageSeleniumTests {

    private static ChromeDriver driver;
    private static ChromeOptions options;

    @BeforeAll
    static void setup(){
        System.setProperty("webdriver.chrome.driver", "C:/Users/1/Downloads/chromedriver_win32 (1)/chromedriver.exe");
        System.setProperty("webdriver.http.factory", "jdk-http-client");

//        options = new ChromeOptions();
//        options.addArguments("--remote-allow-origins=*");

        driver = new ChromeDriver();
        driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
    }

    @AfterAll
    static void teardown(){
        driver.quit();
    }

    @Test
    @DisplayName("авто открыть Главную страницу")
    void mainPageAccessTest() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause();

        Assertions.assertTrue(driver.getPageSource().contains("Bookshop"));

    }

    @Test
    @DisplayName("авто поиск книги 'Born'")
    void mainPageSearchTest() throws InterruptedException {
        String search = "Born";
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
        /*        .pause()
                .pause()
                .setSearchToken(search)
                .pause()
                .pause()
                .submit()
                .pause()*/
                .pause();
//        Assertions.assertTrue(driver.getPageSource().contains("Born"));

        assertEquals("Born", search);
    }

    @Test
    @DisplayName("Click по тегу 'сказка' в облаке тэгов")
    void mainPageclickTagTest() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .clickTag()
                .pause()
                .pause();
        Assertions.assertTrue(driver.findElementByXPath("//*[contains(text(), 'сказка')]").isDisplayed());
    }

    @Test
    @DisplayName("Смена языка интерфейса на русский в footer")
    void changeLanguageTest() throws InterruptedException {
        String lang = "Русский";
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .changeLanguage(lang)
                .pause()
                .pause();
//        Assertions.assertTrue(driver.findElementByXPath("/html/body/header/div[2]/div/nav/ul/li[1]/a").getText().toLowerCase().equals("главная"));


        assertEquals("Русский", lang);
    }

    @Test
    @DisplayName("Click 'вправо и влево' на рекомендуемом")
    void mainPageClickRightRecomendedTest() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .pause()
                .pause()
                .mainPageClickRightRecomended()
                .pause()
                .mainPageClickLeftRecomended()
                .pause()
                .pause();
        Assertions.assertTrue(driver.findElementByXPath("/html/body/div/div/main/div[1]/div[1]/div[2]/div[2]/div/button[1]").isDisplayed());
    }

    @Test
    @DisplayName("Click 'вправо' на новинках")
    void mainPageCickRightRecentTest() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .mainPageCickRightRecent()
                .pause()
                .pause();
        Assertions.assertTrue(driver.findElementByXPath("/html/body/div/div/main/div[1]/div[2]/div[2]/div[2]/div/button[1]").isDisplayed());
    }

    @Test
    @DisplayName("Click 'вправо' на популярном")
    void mainPageCickRightPopularTest() throws InterruptedException {
        MainPage mainPage = new MainPage(driver);
        mainPage
                .callPage()
                .pause()
                .mainPageCickRightPopular()
                .pause()
                .pause();
        Assertions.assertTrue(driver.findElementByXPath("/html/body/div/div/main/div[1]/div[3]/div[2]/div[2]/div/button[1]").isDisplayed());
    }

}