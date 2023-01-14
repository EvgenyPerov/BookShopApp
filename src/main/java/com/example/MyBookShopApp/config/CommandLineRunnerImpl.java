package com.example.MyBookShopApp.config;

import com.example.MyBookShopApp.data.BookRepository;
import com.example.MyBookShopApp.data.TestEntity;
import com.example.MyBookShopApp.data.TestEntityCrudRepository;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.logging.Logger;

@Configuration
public class CommandLineRunnerImpl implements CommandLineRunner {

    private TestEntityCrudRepository repository;
    private BookRepository bookRepository;

    @Autowired
    public CommandLineRunnerImpl(TestEntityCrudRepository repository, BookRepository bookRepository) {
        this.repository = repository;
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        for (int i = 0; i < 5; i++) {
            createTestEntity(new TestEntity());
        }

//        TestEntity readTestEntity = readTestEntityById(3L);
//        if (readTestEntity != null){
//            Logger.getLogger(CommandLineRunnerImpl.class.getSimpleName()).info("read "+ readTestEntity.toString());
//        } else throw  new NullPointerException();
//
//        TestEntity updateTestEntity = updateTestEntity(4L);
//        if (updateTestEntity != null){
//            Logger.getLogger(CommandLineRunnerImpl.class.getSimpleName()).info("update "+ updateTestEntity.toString());
//        }
//
//        deleteTestEntityById(3L);

//        Logger.getLogger(CommandLineRunnerImpl.class.getSimpleName())
//                .info("customFindAll "+ bookRepository.customFindAllBooks().size());
    }

    private void createTestEntity(TestEntity entity) {
        entity.setData(entity.getClass().getSimpleName() + entity.hashCode());
        repository.save(entity);
    }

    private TestEntity readTestEntityById(Long id) {
        return repository.findTestEntityById(id);
    }

    private TestEntity updateTestEntity(Long id) {
        TestEntity testEntity = readTestEntityById(id);
        testEntity.setData(testEntity.getData()+ "NEW DATA!!!");
        repository.save(testEntity);
        return testEntity;
    }

    private void deleteTestEntityById(Long id) {
       repository.deleteById(id);
    }
}