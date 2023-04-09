package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.data.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestEntityCrudRepository extends JpaRepository<TestEntity, Long> {

    TestEntity findTestEntityById(Long id);
}
