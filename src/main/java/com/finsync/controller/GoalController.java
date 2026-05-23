package com.finsync.controller;

import com.finsync.model.FinancialGoal;
import com.finsync.service.GoalService;
import com.finsync.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/goals")
public class GoalController {

    private final GoalService goalService;
    private final UserService userService;

    public GoalController(GoalService goalService, UserService userService) {
        this.goalService = goalService;
        this.userService = userService;
    }

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        int userId = userService.findByEmail(userDetails.getUsername()).getId();
        List<FinancialGoal> goals = goalService.getByUser(userId);
        BigDecimal totalTarget = goals.stream()
                .map(g -> g.getTargetAmount() != null ? g.getTargetAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCurrent = goals.stream()
                .map(g -> g.getCurrentAmount() != null ? g.getCurrentAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        model.addAttribute("goals", goals);
        model.addAttribute("goalCount", goals.size());
        model.addAttribute("totalTarget", totalTarget);
        model.addAttribute("totalCurrent", totalCurrent);
        return "goals/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("goal", new FinancialGoal());
        return "goals/form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable int id, Model model) {
        model.addAttribute("goal", goalService.getById(id));
        return "goals/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute FinancialGoal goal,
                       @AuthenticationPrincipal UserDetails userDetails) {
        int userId = userService.findByEmail(userDetails.getUsername()).getId();
        goal.setUserId(userId);
        goalService.save(goal);
        return "redirect:/goals";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable int id) {
        goalService.delete(id);
        return "redirect:/goals";
    }
}
