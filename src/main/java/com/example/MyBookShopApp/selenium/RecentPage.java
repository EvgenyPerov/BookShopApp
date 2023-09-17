package com.example.MyBookShopApp.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecentPage {

    private String url = "http://localhost:8085/books/recent";

    private ChromeDriver driver;
    public RecentPage(ChromeDriver driver) {
        this.driver = driver;
    }


    public RecentPage callPage() {
        driver.get(url);
        return this;
    }

    public RecentPage pause() throws InterruptedException {
        Thread.sleep(2000);
        return this;
    }

    public RecentPage clickDateFrom() {
        WebElement webElementFrom = driver.findElement(By.id("fromdaterecent"));
//        By.xpath("/html/body/div[1]/div/main/div/div[1]/form/div[1]/input")

        webElementFrom.click();
        return this;
    }

    public RecentPage clickDateMonthsFrom() {
        WebElement webElementFrom = driver.findElement(By.xpath("//*[@id=\"datepickers-container\"]/div[1]/nav/div[2]"));
                                                                             //*[@id="datepickers-container"]/div[1]/nav/div[2]
        webElementFrom.click();
        return this;
    }

    public RecentPage clickDateYearFrom() {
        WebElement webElementFrom = driver.findElement(By.xpath("//*[@id=\"datepickers-container\"]/div[1]/nav/div[2]"));
        webElementFrom.click();
        return this;
    }
    public RecentPage setDateYearFrom(String numberOfYear) {
        int correctYearNumberForChrome =  Integer.valueOf(numberOfYear.substring(numberOfYear.length()-1)) + 2;
        WebElement webElementFrom = driver.findElement(
                By.xpath("//*[@id=\"datepickers-container\"]/div[1]/div/div[3]/div/div["+ correctYearNumberForChrome+ "]"));
        webElementFrom.click();
        return this;
    }

    public RecentPage setDateMonthFrom(String numberOfMonth) {
        WebElement webElementFrom = driver.findElement(By.xpath("//*[@id=\"datepickers-container\"]/div[1]/div/div[2]/div/div[" +numberOfMonth+ "]"));
        webElementFrom.click();
        return this;
    }

    public RecentPage setDateDayFrom(String numberOfDay) {
        int correctDayNumberForChrome =  Integer.valueOf(numberOfDay) + 2;
        WebElement webElementFrom = driver.findElement
                (By.xpath("//*[@id=\"datepickers-container\"]/div[1]/div/div[1]/div[2]/div["+correctDayNumberForChrome+"]"));
        webElementFrom.click();
        return this;
    }

    //*******************************************

    public RecentPage clickDateTo() {
        WebElement webElementFrom = driver.findElement(By.id("enddaterecent"));
        webElementFrom.click();
        return this;
    }

    public RecentPage clickDateMonthsTo() {
        WebElement webElementFrom = driver.findElement(By.xpath("//*[@id=\"datepickers-container\"]/div[2]/nav/div[2]"));
        webElementFrom.click();
        return this;
    }

    public RecentPage clickDateYearTo() {
        WebElement webElementFrom = driver.findElement(By.xpath("//*[@id=\"datepickers-container\"]/div[2]/nav/div[2]"));
        webElementFrom.click();
        return this;
    }
    public RecentPage setDateYearTo(String numberOfYear) {
        int correctYearNumberForChrome =  Integer.valueOf(numberOfYear.substring(numberOfYear.length()-1)) + 2;
        WebElement webElementFrom = driver.findElement(
                By.xpath("//*[@id=\"datepickers-container\"]/div[2]/div/div[3]/div/div["+ correctYearNumberForChrome+ "]"));
        webElementFrom.click();
        return this;
    }

    public RecentPage setDateMonthTo(String numberOfMonth) {
        WebElement webElementFrom = driver.findElement(By.xpath("//*[@id=\"datepickers-container\"]/div[2]/div/div[2]/div/div[" +numberOfMonth+ "]"));
        webElementFrom.click();
        return this;
    }

    public RecentPage setDateDayTo(String numberOfDay) {
        int correctDayNumberForChrome =  Integer.valueOf(numberOfDay) + 2;
        WebElement webElementFrom = driver.findElement
                (By.xpath("//*[@id=\"datepickers-container\"]/div[2]/div/div[1]/div[2]/div["+correctDayNumberForChrome+"]"));
        webElementFrom.click();
        return this;
    }


}
