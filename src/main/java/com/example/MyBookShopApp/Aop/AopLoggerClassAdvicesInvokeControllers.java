package com.example.MyBookShopApp.Aop;

import com.example.MyBookShopApp.data.dto.SearchWordDto;
import com.example.MyBookShopApp.errs.BadRequestException;
import com.example.MyBookShopApp.struct.book.book.Book;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.logging.Logger;

@Aspect
@Component
public class AopLoggerClassAdvicesInvokeControllers {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    //выполнение совета после успешного выполнения метода
    @AfterReturning(value = "execution(public * getRecomendedBooksOnMainPage(String, String,Integer, Integer))", returning = "page")
    public void loggGetRecomendedBooksOnMainPage(JoinPoint joinPoint, Page<Book> page){
        if (joinPoint != null) logger.info(joinPoint + " Method 'getRecomendedBooksOnMainPage' start executing...");
        if (page != null)  logger.info("Count of recommended books = " + page.getContent().size());
    }

    //выполнение совета после не успешного выполнения метода
    @AfterThrowing(value = "execution(public * getRecomendedBooksOnMainPage(String, String,Integer, Integer))", throwing = "ex")
    public void loggGetRecomendedBooksOnMainPage(JoinPoint joinPoint, Exception ex){
        if (joinPoint != null) logger.info(joinPoint + " Method 'getRecomendedBooksOnMainPage' NOT start executing, becouse ERROR: " + ex.getLocalizedMessage());
    }

    //выполнение совета во время выполнения метода (вместо Before и After)
    @Around(value = "execution(public String getSlugOfBook(..))")
    public Object loggGetSlugOfBook(ProceedingJoinPoint joinPoint) throws BadRequestException {
        Object returnValue = null;

        if (joinPoint != null) {
            logger.info(joinPoint + " Method 'getSlugOfBook' start executing...");
            try {
                returnValue = joinPoint.proceed();
            } catch (Throwable e) {
                throw new BadRequestException(e.getLocalizedMessage());
            }

            logger.info(joinPoint + " Method 'getSlugOfBook' finish executing...");
        }
        return  returnValue;
    }
    @Pointcut(value = "within(com.example.MyBookShopApp.controllers.*)")
    public void allMethodsAllControllersPrintClassnamePointcut(){}

    @Before("allMethodsAllControllersPrintClassnamePointcut()")
    public void allMethodsAllControllersPrintClassnameAdvice(JoinPoint joinPoint){
        logger.info("JoinPoint invoke controller class: " + joinPoint.getTarget().getClass().getSimpleName());
    }

    @Pointcut(value = "within(com.example.MyBookShopApp.controllers.BooksController)")
    public void allMethodsOfBooksControllerPointcut(){}

    @Before("allMethodsOfBooksControllerPointcut()")
    public void startMethodInBooksControllerAdvice(){
        logger.info("Start some method in BooksController");
    }

    @After("allMethodsOfBooksControllerPointcut()")
    public void finishMethodInBooksControllerAdvice(){
        logger.info("Finish some method in BooksController");
    }

    @Pointcut(value = "within(com.example.MyBookShopApp.controllers.MainPageController)")
    public void allMethodsOfMainPageControllerPointcut(){}

    @Before("allMethodsOfMainPageControllerPointcut()")
    public void startMethodInMainPageControllerAdvice(){
        logger.info("Start some method in MainPageController");
    }

    @AfterReturning("allMethodsOfMainPageControllerPointcut() && args(searchWordDto,model)")
    public void printValuesInSomeMethodOfMainPageControllerAdvice(SearchWordDto searchWordDto, Model model){
        logger.info("catched incoming value 'searchWordDto'=  " + searchWordDto.getExample());
    }

    @After("allMethodsOfMainPageControllerPointcut() && args(model, cartContents,postponedContents)")
    public void printValuesInSomeMethodOfMainPageControllerAdvice(Model model, String cartContents, String postponedContents){
        logger.info("catched incoming value 'cartContents'=  " + cartContents);
        logger.info("catched incoming value 'postponedContents'=  " + postponedContents);
    }

    @After("allMethodsOfMainPageControllerPointcut() && args(cartContents,postponedContents,offset,limit)")
    public void printValuesInSomeMethodOfMainPageControllerAdvice(String cartContents, String postponedContents, Integer offset, Integer limit){
        logger.info("catched incoming value 'cartContents'=  " + cartContents);
        logger.info("catched incoming value 'postponedContents'=  " + postponedContents);
        logger.info("catched incoming value 'offset'=  " + offset);
        logger.info("catched incoming value 'limit'=  " + limit);
    }

    @Pointcut(value = "@annotation(com.example.MyBookShopApp.Aop.annotations.SelectRecentDateAnnotation)")
    public void selectRecentDatePointcut(){}

    @Before("selectRecentDatePointcut() && args(from,to,offset,limit)")
    public void printHelloOnRecentPageAdvice(String from, String to, Integer offset, Integer limit){
        if (from == null || to == null){
            from = "Today";
            to = "Last month";
        }
        logger.info("You see page News with parameters: data From - " + from + ", data To - " + to);
    }

}
