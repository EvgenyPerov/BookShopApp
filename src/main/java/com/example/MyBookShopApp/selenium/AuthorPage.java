package com.example.MyBookShopApp.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

public class AuthorPage {

    private String url = "http://localhost:8085/authors";

    private ChromeDriver driver;
    public AuthorPage(ChromeDriver driver) {
        this.driver = driver;
    }


    public AuthorPage callPage() {
        driver.get(url);
        return this;
    }

    public AuthorPage pause() throws InterruptedException {
        Thread.sleep(2000);
        return this;
    }

    public AuthorPage clickAuthor(String name) {
        var webElement = driver.findElement(By.xpath("//*[contains(text(), '"+name+"')]"));
        webElement.click();
        return this;
    }

}
