package com.example.MyBookShopApp.security;

import com.example.MyBookShopApp.data.repo.UserRepository;
import com.example.MyBookShopApp.struct.user.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.logging.Logger;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user =  super.loadUser(userRequest);
        logger.info("CustomOAuth2UserService invoked");
        return new CustomOAuth2User(user);
    }

    public void addOAuthUser(CustomOAuth2User oauthUser) {
        var existUser = userRepository.findUserEntityByEmail(oauthUser.getEmail());
        if (existUser == null) {
            var user = new UserEntity();
            user.setName(oauthUser.getName());
            user.setEmail(oauthUser.getEmail());

            user.setPhone(oauthUser.getPhone());

            user.setPassword(oauthUser.getAddress());

            user.setRegTime(LocalDateTime.now());
            user.setHash(String.valueOf(this.hashCode()));
            user.setBalance(0);

            userRepository.save(user);

        }
    }

}