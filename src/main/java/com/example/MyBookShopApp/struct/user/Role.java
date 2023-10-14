package com.example.MyBookShopApp.struct.user;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "role")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "VARCHAR(255) NOT NULL")
    private String roleName;


    @Transient
    @ManyToMany(mappedBy = "roles")
    private Set<UserEntity> users = new HashSet<>();

    public Role() {

    }

    @Override
    public String getAuthority() {
        return getRoleName();
    }


    public Role(String roleName) {
        this.roleName = roleName;
    }
}
