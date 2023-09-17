package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.other.DocumentEntity;
import com.example.MyBookShopApp.struct.other.FaqEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaqRepository extends JpaRepository<FaqEntity, Integer> {


}
