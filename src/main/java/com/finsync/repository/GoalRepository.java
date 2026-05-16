package com.finsync.repository;

import com.finsync.model.FinancialGoal;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GoalRepository extends CrudRepository<FinancialGoal, Integer> {

    @Query("SELECT * FROM financial_goals WHERE user_id = :userId")
    List<FinancialGoal> findByUserId(int userId);
}
