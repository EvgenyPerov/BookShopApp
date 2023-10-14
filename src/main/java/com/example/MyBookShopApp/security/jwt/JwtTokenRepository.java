package com.example.MyBookShopApp.security.jwt;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtEntity, Long> {

    JwtEntity findByTokenHashIs(int tokenHash);

    @Modifying
    @Transactional
    @Query(value ="DELETE FROM black_list WHERE black_list.date <:pastDate", nativeQuery = true)
    void deleteOldJwtToken(@Param("pastDate") Date pastDate);


}
