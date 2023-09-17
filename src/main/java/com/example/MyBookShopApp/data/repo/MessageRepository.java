package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.book.review.MessageEntity;
import com.example.MyBookShopApp.struct.other.FaqEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageEntity, Integer> {


}
