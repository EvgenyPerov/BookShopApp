package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.book.file.BookFileEntity;
import com.example.MyBookShopApp.struct.book.file.FileDownloadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileDownloadRepository extends JpaRepository<FileDownloadEntity, Integer> {

    FileDownloadEntity findByBookIdAndUserId(int bookId, int userId);

}
