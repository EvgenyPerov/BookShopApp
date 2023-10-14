package com.example.MyBookShopApp.data;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TestEntityCrudRepository extends JpaRepository<TestEntity, Long> {

    TestEntity findTestEntityById(Long id);
}
