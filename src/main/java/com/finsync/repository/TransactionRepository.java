package com.finsync.repository;

import com.finsync.model.Transaction;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {

    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY operation_date DESC")
    List<Transaction> findByUserId(int userId);

    @Query("SELECT * FROM transactions WHERE user_id = :userId AND type = :type ORDER BY operation_date DESC")
    List<Transaction> findByUserIdAndType(int userId, String type);
}
