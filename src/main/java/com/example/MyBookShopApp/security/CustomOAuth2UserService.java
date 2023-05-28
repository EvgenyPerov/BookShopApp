package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.repo.UserRepository;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user =  super.loadUser(userRequest);
        System.out.println("CustomOAuth2UserService invoked");
        return new CustomOAuth2User(user);
    }

    public void addOAuthUser(CustomOAuth2User oauthUser) {
        UserEntity existUser = userRepository.findUserEntityByEmail(oauthUser.getEmail());
        if (existUser == null) {
            UserEntity user = new UserEntity();
            user.setName(oauthUser.getName());
            user.setEmail(oauthUser.getEmail());

            user.setPhone(oauthUser.getPhone());
            System.out.println("Телефон: " + oauthUser.getPhone());

            user.setPassword(oauthUser.getAddress());
            System.out.println("Адрес: " + oauthUser.getAddress());

            System.out.println("Телефон2: " + oauthUser.getPassword());

            user.setRegTime(LocalDateTime.now());
            user.setHash(String.valueOf(this.hashCode()));
            user.setBalance(0);

            userRepository.save(user);
            System.out.println("Created new userAuth: " + user.getName());

            System.out.println("Attributes: " + oauthUser.getAuthorities().size());
            oauthUser.getAuthorities().forEach((k) -> System.out.println());
        }
    }

}