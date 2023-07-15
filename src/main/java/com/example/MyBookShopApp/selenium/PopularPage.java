package com.example.MyBookShopApp.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class PopularPage {

    private String url = "http://localhost:8085/books/popular";

    private ChromeDriver driver;
    public PopularPage(ChromeDriver driver) {
        this.driver = driver;
    }


    public PopularPage callPage() {
        driver.get(url);
        return this;
    }

    public PopularPage pause() throws InterruptedException {
        Thread.sleep(2000);
        return this;
    }

    public PopularPage clickBook() {
        WebElement webElement = driver.findElement(By.xpath("//*[contains(text(), 'Heartbeat')]"));
        webElement.click();
        return this;
    }

}
