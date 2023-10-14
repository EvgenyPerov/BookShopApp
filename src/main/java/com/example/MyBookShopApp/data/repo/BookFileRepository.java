package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.book.file.BookFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookFileRepository extends JpaRepository<BookFileEntity, Integer> {

    BookFileEntity findBookFileEntityByHash(String hash);

}
