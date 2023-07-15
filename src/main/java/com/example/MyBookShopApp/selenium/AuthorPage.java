package com.example.MyBookShopApp.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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
//        WebElement webElement = driver.findElement(By.xpath("/html/body/div/div/main/div/div[1]/div[2]/div[1]/div[1]/a"));
        WebElement webElement = driver.findElement(By.xpath("//*[contains(text(), '"+name+"')]"));
        webElement.click();
        return this;
    }

}
