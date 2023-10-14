package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.book.file.BookFileTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookFileTypeRepository extends JpaRepository<BookFileTypeEntity, Integer> {

    BookFileTypeEntity findByNameIsIgnoreCase(String name);

}
