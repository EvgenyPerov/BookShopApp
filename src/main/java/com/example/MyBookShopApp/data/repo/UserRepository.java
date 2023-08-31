package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    UserEntity findByHashIs(String hash);

    UserEntity findUserEntityByEmail(String email);

    UserEntity findUserEntityByName(String name);

    UserEntity findUserEntityByPhone(String phone);
}
