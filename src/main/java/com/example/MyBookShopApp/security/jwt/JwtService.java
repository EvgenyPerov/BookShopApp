package com.example.MyBookShopApp.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private Date today;

    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    public boolean addTokenToBlacklist(String token) {
//        System.out.println("Hash токена в Blacklist - " + token.hashCode());
        clearOldTokenFromBlacklist();

        if (jwtTokenRepository.findByTokenHashIs(token.hashCode()) == null) {
            JwtEntity jwt = new JwtEntity();
            jwt.setDate(new Date());
            jwt.setTokenHash(token.hashCode());

            jwtTokenRepository.save(jwt);
            return true;
        }

        return false;
    }

    private void clearOldTokenFromBlacklist(){
        final LocalDateTime now = LocalDateTime.now();
        final Instant updateDateBlacklistInstant = now.minusDays(33).atZone(ZoneId.systemDefault()).toInstant();
        Date pastDate = Date.from(updateDateBlacklistInstant);

        jwtTokenRepository.deleteOldJwtToken(pastDate);
    }

    public boolean isTokenInBlacklist(String token){
       return jwtTokenRepository.findByTokenHashIs(token.hashCode()) != null;
    }

}
