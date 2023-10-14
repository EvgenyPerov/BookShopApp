package com.example.MyBookShopApp.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

public class PostponedPage {

    private String urlBook13 = "http://localhost:8085/books/13";
    private String urlPost = "http://localhost:8085/books/postponed";

    private String urlCart = "http://localhost:8085/books/cart";

    private ChromeDriver driver;
    public PostponedPage(ChromeDriver driver) {
        this.driver = driver;
    }


    public PostponedPage callPageBook() {
        driver.get(urlBook13);
        return this;
    }

    public PostponedPage callPagePost() {
        driver.get(urlPost);
        return this;
    }

    public PostponedPage callPageCart() {
        driver.get(urlCart);
        return this;
    }


    public PostponedPage pause() throws InterruptedException {
        Thread.sleep(2000);
        return this;
    }

    public PostponedPage changeBookStatus(String bookStatus) {
        var webElement = driver.findElement(By.id(bookStatus));
        webElement.click();
        return this;
    }
}
