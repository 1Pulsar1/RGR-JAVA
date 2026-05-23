package com.finsync.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table("financial_goals")
public class FinancialGoal {

    @Id
    private Integer id;

    @Column("user_id")
    private Integer userId;

    private String title;

    @Column("target_amount")
    private BigDecimal targetAmount;

    @Column("current_amount")
    private BigDecimal currentAmount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate deadline;

    public FinancialGoal() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public BigDecimal getTargetAmount() { return targetAmount; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }

    public BigDecimal getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(BigDecimal currentAmount) { this.currentAmount = currentAmount; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public int getProgressPercent() {
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) == 0) return 0;
        BigDecimal cur = currentAmount != null ? currentAmount : BigDecimal.ZERO;
        int pct = cur.multiply(BigDecimal.valueOf(100)).divide(targetAmount, 0, java.math.RoundingMode.FLOOR).intValue();
        return Math.min(pct, 100);
    }
}
