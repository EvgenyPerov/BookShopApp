package com.example.MyBookShopApp.data.services;

import com.example.MyBookShopApp.data.repo.Book2UserRepository;
import com.example.MyBookShopApp.data.repo.Book2UserTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Book2UserTypeService {

  private Book2UserTypeRepository book2UserTypeRepository;

  @Autowired
    public Book2UserTypeService(Book2UserTypeRepository book2UserTypeRepository) {
        this.book2UserTypeRepository = book2UserTypeRepository;
    }

    public int getIdByType(String code){
      return book2UserTypeRepository.findByCodeIs(code).getId();
    }



}
