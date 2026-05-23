package com.finsync.controller;

import com.finsync.service.GoalService;
import com.finsync.service.TransactionService;
import com.finsync.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;

@Controller
public class DashboardController {

    private final TransactionService transactionService;
    private final GoalService goalService;
    private final UserService userService;

    public DashboardController(TransactionService transactionService,
                               GoalService goalService,
                               UserService userService) {
        this.transactionService = transactionService;
        this.goalService = goalService;
        this.userService = userService;
    }


    @GetMapping("/")
    public String root() {
        return "redirect:/auth/register";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        int userId = userService.findByEmail(userDetails.getUsername()).getId();

        BigDecimal totalIncome = transactionService.getTotalByType(userId, "INCOME");
        BigDecimal totalExpense = transactionService.getTotalByType(userId, "EXPENSE");
        BigDecimal balance = totalIncome.subtract(totalExpense);

        model.addAttribute("balance", balance);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("recentTransactions", transactionService.getLastN(userId, 5));
        model.addAttribute("goals", goalService.getByUser(userId));
        model.addAttribute("categories", transactionService.getAllCategories());

        return "index";
    }

}