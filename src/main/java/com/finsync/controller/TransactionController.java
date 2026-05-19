package com.finsync.controller;

import com.finsync.model.Transaction;
import com.finsync.service.TransactionService;
import com.finsync.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionController(TransactionService transactionService, UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        int userId = userService.findByEmail(userDetails.getUsername()).getId();
        model.addAttribute("transactions", transactionService.getByUser(userId));
        model.addAttribute("categories", transactionService.getAllCategories());
        return "transactions/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("transaction", new Transaction());
        model.addAttribute("categories", transactionService.getAllCategories());
        return "transactions/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("transaction", transactionService.getById(id));
        model.addAttribute("categories", transactionService.getAllCategories());
        return "transactions/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Transaction transaction,
                       @AuthenticationPrincipal UserDetails userDetails) {
        int userId = userService.findByEmail(userDetails.getUsername()).getId();
        transaction.setUserId(userId);
        if (transaction.getOperationDate() == null) {
            transaction.setOperationDate(LocalDateTime.now());
        }
        transactionService.save(transaction);
        return "redirect:/transactions";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable int id) {
        transactionService.delete(id);
        return "redirect:/transactions";
    }
}
