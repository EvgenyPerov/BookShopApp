package com.example.MyBookShopApp.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;

public class GenrePage {

    private String url = "http://localhost:8085/genres";

    private ChromeDriver driver;
    public GenrePage(ChromeDriver driver) {
        this.driver = driver;
    }


    public GenrePage callPage() {
        driver.get(url);
        return this;
    }

    public GenrePage pause() throws InterruptedException {
        Thread.sleep(2000);
        return this;
    }
    public GenrePage clickGenre() {
        var webElement = driver.findElement(By.xpath("//*[contains(text(), 'Лёгкое чтение')]"));
        webElement.click();
        return this;
    }

}
