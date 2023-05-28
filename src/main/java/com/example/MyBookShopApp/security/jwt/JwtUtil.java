package com.example.MyBookShopApp.security.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {

    @Value("${auth.secret}")
    private String secret;

    @Value("${auth.secretRefresh}")
    private String secretRefresh;


    public String createToken(Map<String, Object> claims, String username){
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant = now.plusMonths(1).atZone(ZoneId.systemDefault()).toInstant();
        final Date accessExpiration = Date.from(accessExpirationInstant);
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(accessExpiration)
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    public String generateRefreshToken(Map<String, Object> claims, String username) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts
                .builder()
//                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(refreshExpiration)
                .signWith(SignatureAlgorithm.HS256, secretRefresh).compact();
    }

        public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
            Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
    }
    private Claims extractAllClaims(String token){
        try {
            Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
            return claims;
        } catch (ExpiredJwtException expEx) {
            System.out.println("Token expired> " + expEx);
        } catch (UnsupportedJwtException unsEx) {
            System.out.println("Unsupported jwt> " + unsEx);
        } catch (MalformedJwtException mjEx) {
            System.out.println("Malformed jwt> " + mjEx);
        } catch (SignatureException sEx) {
            System.out.println("Invalid signature> " + sEx);
        } catch (Exception e) {
            System.out.println("invalid token> " + e);
        }
        return null;
    }

    private Claims extractRefreshAllClaims(String token){
        try {
            Claims claims = Jwts.parser().setSigningKey(secretRefresh).parseClaimsJws(token).getBody();
            return claims;
        } catch (ExpiredJwtException expEx) {
            System.out.println("Token expired> " + expEx);
        } catch (UnsupportedJwtException unsEx) {
            System.out.println("Unsupported jwt> " + unsEx);
        } catch (MalformedJwtException mjEx) {
            System.out.println("Malformed jwt> " + mjEx);
        } catch (SignatureException sEx) {
            System.out.println("Invalid signature> " + sEx);
        } catch (Exception e) {
            System.out.println("invalid token> " + e);
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
    } //

    public Boolean validateToken(String token, UserDetails userDetails){ //
        String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Boolean validateRefreshToken(String refreshToken, UserDetails userDetails){ //
        String username = extractUserName(refreshToken);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(refreshToken));
    }

}
