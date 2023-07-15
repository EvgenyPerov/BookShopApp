package com.example.MyBookShopApp.selenium;

import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class SignupPageSeleniumTests {


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
    @DisplayName("Регистрация")
    public void signupPageTest() throws InterruptedException {
        String name = "TestUserName",  phone = "+79015554433", mail = "test@mail.ru", pass = "1234567";
        SignupPage page = new SignupPage(driver);
        page
                .callPageSignup()
                .pause()
                .setName(name)
                .pause()
                .setPhone(phone)
                .pause()
                .setEmail(mail)
                .pause()
                .submitPhone()
                .pause()
                .setPhoneCode()
                .pause()
                .submitEmail()
                .pause()
                .setMailCode()
                .pause()
                .setPassword(pass)
                .pause()
                .clickRegistration()
                .pause()
                .pause()
                .pause();

        Assertions.assertTrue(driver.findElementByXPath("/html/body/div/div[1]/div/h1").getText().equals("ВХОД"));
    }

    @Test
    @DisplayName("Вход, профиль, выход")
    public void signinProfileLogoutPageTest() throws InterruptedException {
        String name = "TestUserName", mail = "test@mail.ru", pass = "1234567";
        SignupPage page = new SignupPage(driver);
        page
                .callPageSignin()
                .pause()
                .setTypeAuth()
                .pause()
                .inputmail(mail)
                .pause()
                .clickNext()
                .pause()
                .inputPass(pass)
                .pause()
                .clickSignin()
                .pause()
                .clickProfile()
                .pause()
                .clickLogout()
                .pause()
                .pause();

//        Assertions.assertTrue(driver.getPageSource().contains(name));
        Assertions.assertTrue(driver.findElementByXPath("/html/body/div/div[1]/div/h1").getText().equals("ВХОД"));
    }
}