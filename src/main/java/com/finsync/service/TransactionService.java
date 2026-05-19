package com.finsync.service;

import com.finsync.model.Category;
import com.finsync.model.Transaction;
import com.finsync.repository.CategoryRepository;
import com.finsync.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Transaction> getByUser(int userId) {
        return transactionRepository.findByUserId(userId);
    }

    public List<Transaction> getLastN(int userId, int n) {
        List<Transaction> all = transactionRepository.findByUserId(userId);
        return all.size() > n ? all.subList(0, n) : all;
    }

    public BigDecimal getTotalByType(int userId, String type) {
        return transactionRepository.findByUserIdAndType(userId, type)
                .stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void save(Transaction transaction) {
        if (transaction.getOperationDate() == null) {
            transaction.setOperationDate(LocalDateTime.now());
        }
        transactionRepository.save(transaction);
    }

    public Transaction getById(int id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Транзакция не найдена"));
    }

    public void delete(int id) {
        transactionRepository.deleteById(id);
    }

    public Iterable<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
