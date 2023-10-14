package com.example.MyBookShopApp.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Service
public class JwtService {

    @Autowired
    private JwtTokenRepository jwtTokenRepository;

    public boolean addTokenToBlacklist(String token) {
        clearOldTokenFromBlacklist();

        if (jwtTokenRepository.findByTokenHashIs(token.hashCode()) == null) {
            var jwt = new JwtEntity();
            jwt.setDate(new Date());
            jwt.setTokenHash(token.hashCode());

            jwtTokenRepository.save(jwt);
            return true;
        }

        return false;
    }

    private void clearOldTokenFromBlacklist(){
        final var now = LocalDateTime.now();
        final var updateDateBlacklistInstant = now.minusDays(33).atZone(ZoneId.systemDefault()).toInstant();
        var pastDate = Date.from(updateDateBlacklistInstant);

        jwtTokenRepository.deleteOldJwtToken(pastDate);
    }

    public boolean isTokenInBlacklist(String token){
       return jwtTokenRepository.findByTokenHashIs(token.hashCode()) != null;
    }

}
