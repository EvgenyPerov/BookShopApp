package com.example.MyBookShopApp.security.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;

@Service
public class JwtUtil {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    @Value("${auth.secret}")
    private String secret;

    public String createToken(Map<String, Object> claims, String username){
        final var now = LocalDateTime.now();
        final var accessExpirationInstant = now.plusMonths(1).atZone(ZoneId.systemDefault()).toInstant();
        final var accessExpiration = Date.from(accessExpirationInstant);
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(accessExpiration)
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }

        public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
            var claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token){
        try {
            return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException expEx) {
            logger.info("Token expired> " + expEx);
        } catch (UnsupportedJwtException unsEx) {
            logger.info("Unsupported jwt> " + unsEx);
        } catch (MalformedJwtException mjEx) {
            logger.info("Malformed jwt> " + mjEx);
        } catch (SignatureException sEx) {
            logger.info("Invalid signature> " + sEx);
        } catch (Exception e) {
            logger.info("invalid token> " + e);
        }
        return null;
    }

    public String extractUserName(String token){
            return extractClaim(token, Claims::getSubject);
    }
    public Date extractExpiration(String token)  { //
            return extractClaim(token, Claims::getExpiration);
    }

    public Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails){ //
        String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
