package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    Role findByRoleName(String name);
}
