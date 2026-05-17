package com.finsync.service;

import com.finsync.model.FinancialGoal;
import com.finsync.repository.GoalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoalService {

    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    public List<FinancialGoal> getByUser(int userId) {
        return goalRepository.findByUserId(userId);
    }

    public FinancialGoal getById(int id) {
        return goalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Цель не найдена"));
    }

    public void save(FinancialGoal goal) {
        goalRepository.save(goal);
    }

    public void delete(int id) {
        goalRepository.deleteById(id);
    }
}
