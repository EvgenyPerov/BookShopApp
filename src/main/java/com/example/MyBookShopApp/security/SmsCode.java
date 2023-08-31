package com.example.MyBookShopApp.security;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Data
@Table(name = "sms_keys")
public class SmsCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private LocalDateTime expireTime;

    public SmsCode(String code, Integer expireTimeIn) {
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireTimeIn) ;
    }

    public boolean isExpired(){
        return LocalDateTime.now().isAfter(expireTime);
    }
}
