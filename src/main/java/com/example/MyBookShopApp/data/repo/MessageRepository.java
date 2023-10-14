package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.book.review.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {


}
