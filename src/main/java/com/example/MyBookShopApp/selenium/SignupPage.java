package com.example.MyBookShopApp.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class SignupPage {

    private String urlSignup = "http://localhost:8085/signup";
    private String urlSignin = "http://localhost:8085/signin";

    private String urlProfile = "http://localhost:8085/profile";

    private ChromeDriver driver;
    public SignupPage(ChromeDriver driver) {
        this.driver = driver;
    }


    public SignupPage callPageSignup() {
        driver.get(urlSignup);
        return this;
    }

    public SignupPage callPageSignin() {
        driver.get(urlSignin);
        return this;
    }

    public SignupPage pause() throws InterruptedException {
        Thread.sleep(2000);
        return this;
    }

    public SignupPage setName(String name) {
        WebElement webElement = driver.findElement(By.id("name"));
        webElement.sendKeys(name);
        return this;
    }

    public SignupPage setPhone(String phone) {
        WebElement webElement = driver.findElement(By.id("phone"));
        webElement.sendKeys(phone);
        return this;
    }

    public SignupPage submitPhone() {
        WebElement webElement = driver.findElement(By.id("submitPhone"));
        webElement.click();
        return this;
    }

    public SignupPage setPhoneCode() {
        WebElement webElement = driver.findElement(By.id("phoneCode"));
        webElement.sendKeys("123456");
        return this;
    }

    public SignupPage setEmail(String mail) {
        WebElement webElement = driver.findElement(By.id("mail"));
        webElement.sendKeys(mail);
        return this;
    }

    public SignupPage submitEmail() {
        WebElement webElement = driver.findElement(By.id("submitMail"));
        webElement.click();
        return this;
    }

    public SignupPage setMailCode() {
        WebElement webElement = driver.findElement(By.id("mailCode"));
        webElement.sendKeys("123456");
        return this;
    }

    public SignupPage setPassword(String password) {
        WebElement webElement = driver.findElement(By.id("pass"));
        webElement.sendKeys(password);
        return this;
    }

    public SignupPage clickRegistration() {
        WebElement webElement = driver.findElement(By.id("registration"));
        webElement.submit();
        return this;
    }

    public SignupPage setTypeAuth(){
        WebElement webElement = driver.findElement(By.xpath("/html/body/div/div[2]/main/form/div/div[1]/div[2]/div/div[2]/label/input"));
        webElement.click();
        return this;
    }

    public SignupPage inputmail(String mail) {
        WebElement webElement = driver.findElement(By.id("mail"));
        webElement.sendKeys(mail);
        return this;
    }

    public SignupPage clickNext() {
        WebElement webElement = driver.findElement(By.id("sendauth"));
        webElement.click();
        return this;
    }

    public SignupPage inputPass(String password) {
        WebElement webElement = driver.findElement(By.id("mailcode"));
        webElement.sendKeys(password);
        return this;
    }

    public SignupPage clickSignin() {
        WebElement webElement = driver.findElement(By.id("toComeInMail"));
        webElement.click();
        return this;
    }

//    public SignupPage clickProfile() {
//        WebElement webElement = driver.findElement(By.xpath("/html/body/header/div[1]/div/div/div[3]/div/a[4]/svg"));
//        webElement.click();
//        return this;
//    }
    public SignupPage callPageProfile() {
        driver.get(urlProfile);
        return this;
    }

    public SignupPage clickLogout() {
        WebElement webElement = driver.findElement(By.xpath("/html/body/header/div[1]/div/div/div[3]/div/a[5]"));
        webElement.click();
        return this;
    }

}
