package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.security.SmsCode;
import com.example.MyBookShopApp.struct.other.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmsCodeRepository extends JpaRepository<SmsCode, Long> {

    SmsCode findSmsCodeByCode(String code);

}
