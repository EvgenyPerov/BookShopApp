package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;

public class BookstoreUserDetailsByPhoneNumber extends BookstoreUserDetails {

    public BookstoreUserDetailsByPhoneNumber(UserEntity user) {
        super(user);
    }

    @Override
    public String getUsername() {
        return getUser().getPhone();
    }
}
