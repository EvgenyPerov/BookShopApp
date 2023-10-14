package com.example.MyBookShopApp.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

public class CartPage {

    private String urlBook13 = "http://localhost:8085/books/13";
    private String urlPost = "http://localhost:8085/books/postponed";
    private String urlCart = "http://localhost:8085/books/cart";

    private ChromeDriver driver;
    public CartPage(ChromeDriver driver) {
        this.driver = driver;
    }


    public CartPage callPageBook() {
        driver.get(urlBook13);
        return this;
    }

    public CartPage callPagePost() {
        driver.get(urlPost);
        return this;
    }

    public CartPage callPageCart() {
        driver.get(urlCart);
        return this;
    }


    public CartPage pause() throws InterruptedException {
        Thread.sleep(2000);
        return this;
    }


    public CartPage changeBookStatus(String bookStatus) {
        var webElement = driver.findElement(By.id(bookStatus));
        webElement.click();
        return this;
    }
}
