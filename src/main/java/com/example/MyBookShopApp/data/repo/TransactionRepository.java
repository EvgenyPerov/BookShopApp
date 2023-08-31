package com.example.MyBookShopApp.data.repo;

import com.example.MyBookShopApp.struct.other.Tag;
import com.example.MyBookShopApp.struct.payments.BalanceTransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<BalanceTransactionEntity, Integer> {

   Page<BalanceTransactionEntity> findAllByUserIdOrderByTimeDesc(int userId , Pageable nextPage);

}
