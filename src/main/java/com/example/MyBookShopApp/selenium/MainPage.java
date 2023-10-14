package com.example.MyBookShopApp.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;

public class MainPage {

    private String url = "http://localhost:8085/";

    private ChromeDriver driver;
    public MainPage(ChromeDriver driver) {
        this.driver = driver;
    }


    public MainPage callPage() {
        driver.get(url);
        JavascriptExecutor js =  driver;
        js.executeScript("document.body.style.zoom='80%'");
        return this;
    }

    public MainPage pause() throws InterruptedException {
        Thread.sleep(2000);
        return this;
    }

    public MainPage setSearchToken(String search) {
        var webElement = driver.findElement(By.id("query"));
        webElement.sendKeys(search);
        return this;
    }

    public MainPage submit() {
        var webElement = driver.findElement(By.id("search"));
        webElement.submit();
        return this;
    }

    public MainPage clickTag() {
        var webElement = driver.findElement(By.xpath("//*[contains(text(), 'сказка')]"));
        webElement.click();
        return this;
    }

    public MainPage changeLanguage(String lang) {
        var webElement = driver.findElement(By.id("locales"));
        webElement.sendKeys(lang);
        return this;
    }

    public MainPage mainPageClickRightRecomended() {
        var webElement = driver.findElement(By.xpath("/html/body/div/div/main/div[1]/div[1]/div[2]/div[2]/div/button[2]"));
        webElement.click();
        return this;
    }

    public MainPage mainPageClickLeftRecomended() {
        var webElement = driver.findElement(By.xpath("/html/body/div/div/main/div[1]/div[1]/div[2]/div[2]/div/button[1]"));
        webElement.click();
        return this;
    }

    public MainPage mainPageCickRightRecent() {
        var webElement = driver.findElement(By.xpath("/html/body/div/div/main/div[1]/div[2]/div[2]/div[2]/div/button[2]"));
        webElement.click();
        return this;
    }

    public MainPage mainPageCickRightPopular() {
        var webElement = driver.findElement(By.xpath("/html/body/div/div/main/div[1]/div[3]/div[2]/div[2]/div/button[2]"));
        webElement.click();
        return this;
    }

}
