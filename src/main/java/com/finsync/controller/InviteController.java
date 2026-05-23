package com.finsync.controller;

import com.finsync.service.InviteService;
import com.finsync.service.TransactionService;
import com.finsync.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/invites")
public class InviteController {

    private final InviteService inviteService;
    private final UserService userService;
    private final TransactionService transactionService;

    public InviteController(InviteService inviteService, UserService userService,
                            TransactionService transactionService) {
        this.inviteService = inviteService;
        this.userService = userService;
        this.transactionService = transactionService;
    }

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        int userId = userService.findByEmail(userDetails.getUsername()).getId();
        model.addAttribute("sent", inviteService.getSent(userId));
        model.addAttribute("received", inviteService.getReceived(userId));

        // собираем данные для сравнительного графика накоплений
        List<Integer> partnerIds = inviteService.getAcceptedPartnerIds(userId);
        List<String> chartLabels = new ArrayList<>();
        List<BigDecimal> chartValues = new ArrayList<>();

        chartLabels.add("Я");
        chartValues.add(transactionService.getSavings(userId));

        for (int partnerId : partnerIds) {
            // TODO: заменить на email когда будет метод
            chartLabels.add("Пользователь #" + partnerId);
            chartValues.add(transactionService.getSavings(partnerId));
        }

        model.addAttribute("chartLabels", chartLabels);
        model.addAttribute("chartValues", chartValues);

        return "invites/list";
    }

    @PostMapping("/send")
    public String send(@RequestParam String email,
                       @AuthenticationPrincipal UserDetails userDetails,
                       Model model) {
        int userId = userService.findByEmail(userDetails.getUsername()).getId();
        try {
            inviteService.send(userId, email);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("sent", inviteService.getSent(userId));
            model.addAttribute("received", inviteService.getReceived(userId));
            return "invites/list";
        }
        return "redirect:/invites";
    }

    @PostMapping("/{id}/accept")
    public String accept(@PathVariable int id) {
        inviteService.accept(id);
        return "redirect:/invites";
    }

    @PostMapping("/{id}/decline")
    public String decline(@PathVariable int id) {
        inviteService.decline(id);
        return "redirect:/invites";
    }
}
