package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.other.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagsRepository extends JpaRepository<Tag, Integer> {

    Tag findTagByIdIs(Integer tagId);

    Tag findByNameIgnoreCase (String name);

}
