package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.other.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<DocumentEntity, Integer> {

    DocumentEntity findBySlug(String slug);

}
