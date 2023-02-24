package com.example.MyBookShopApp.data;

import com.example.MyBookShopApp.struct.genre.GenreEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface GenreRepository extends JpaRepository<GenreEntity, Integer> {

      GenreEntity findGenreEntityById(Integer id);


}
