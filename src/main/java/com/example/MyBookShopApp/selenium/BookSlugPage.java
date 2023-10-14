package com.example.MyBookShopApp.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

public class BookSlugPage {

    private String url = "http://localhost:8085/books/13";
    private String urlPost = "http://localhost:8085/books/postponed";

    private String urlCart = "http://localhost:8085/books/cart";

    private ChromeDriver driver;
    public BookSlugPage(ChromeDriver driver) {
        this.driver = driver;
    }


    public BookSlugPage callPage() {
        driver.get(url);
        return this;
    }

    public BookSlugPage callPagePost() {
        driver.get(urlPost);
        return this;
    }

    public BookSlugPage callPageCart() {
        driver.get(urlCart);
        return this;
    }


    public BookSlugPage pause() throws InterruptedException {
        Thread.sleep(2000);
        return this;
    }

    public BookSlugPage clickAuthor(String name) {
        var webElement = driver.findElement(By.xpath("//*[contains(text(), '"+name+"')]"));
        webElement.click();
        return this;
    }

    public BookSlugPage changeBookStatus(String bookStatus) {
        var webElement = driver.findElement(By.id(bookStatus));
        webElement.click();
        return this;
    }
}
