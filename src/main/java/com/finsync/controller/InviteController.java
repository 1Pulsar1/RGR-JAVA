package com.finsync.controller;

import com.finsync.model.Invite;
import com.finsync.model.User;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        populateModel(userId, model);
        addChartData(userId, model);
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
            populateModel(userId, model);
            addChartData(userId, model);
            return "invites/list";
        }
        return "redirect:/invites";
    }

    @GetMapping("/friends/{friendId}")
    public String friendSession(@PathVariable int friendId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        User me = userService.findByEmail(userDetails.getUsername());

        Invite invite = inviteService.getAcceptedInviteBetween(me.getId(), friendId);
        if (invite == null) {
            return "redirect:/invites";
        }

        User friend = userService.findById(friendId);

        model.addAttribute("friend", friend);
        model.addAttribute("invite", invite);
        model.addAttribute("myEmail", me.getEmail());
        model.addAttribute("mySavings", transactionService.getSavings(me.getId()));
        model.addAttribute("friendSavings", transactionService.getSavings(friendId));
        model.addAttribute("myIncome", transactionService.getTotalByType(me.getId(), "INCOME"));
        model.addAttribute("myExpense", transactionService.getTotalByType(me.getId(), "EXPENSE"));
        model.addAttribute("friendIncome", transactionService.getTotalByType(friendId, "INCOME"));
        model.addAttribute("friendExpense", transactionService.getTotalByType(friendId, "EXPENSE"));
        return "invites/friend-session";
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

    @PostMapping("/{id}/remove")
    public String removeFriend(@PathVariable int id,
                               @AuthenticationPrincipal UserDetails userDetails) {
        int userId = userService.findByEmail(userDetails.getUsername()).getId();
        inviteService.removeFriend(id, userId);
        return "redirect:/invites";
    }

    private void populateModel(int userId, Model model) {
        List<Invite> received = inviteService.getReceived(userId);
        List<Invite> sent = inviteService.getSent(userId);
        List<User> friends = inviteService.getPartners(userId);

        Map<Integer, String> senderEmailMap = new HashMap<>();
        for (Invite inv : received) {
            User u = userService.findById(inv.getSenderId());
            senderEmailMap.put(inv.getSenderId(), u.getEmail());
        }

        Map<Integer, String> receiverEmailMap = new HashMap<>();
        for (Invite inv : sent) {
            User u = userService.findById(inv.getReceiverId());
            receiverEmailMap.put(inv.getReceiverId(), u.getEmail());
        }

        // friendId → inviteId для кнопок удаления
        Map<Integer, Integer> friendInviteIdMap = new HashMap<>();
        for (Invite inv : inviteService.getAcceptedInvites(userId)) {
            int friendId = inv.getSenderId() == userId ? inv.getReceiverId() : inv.getSenderId();
            friendInviteIdMap.put(friendId, inv.getId());
        }

        model.addAttribute("sent", sent);
        model.addAttribute("received", received);
        model.addAttribute("friends", friends);
        model.addAttribute("senderEmailMap", senderEmailMap);
        model.addAttribute("receiverEmailMap", receiverEmailMap);
        model.addAttribute("friendInviteIdMap", friendInviteIdMap);
        model.addAttribute("hasFriends", !friends.isEmpty());
    }

    private void addChartData(int userId, Model model) {
        List<Integer> partnerIds = inviteService.getAcceptedPartnerIds(userId);
        List<String> chartLabels = new ArrayList<>();
        List<BigDecimal> chartValues = new ArrayList<>();
        chartLabels.add("Я");
        chartValues.add(transactionService.getSavings(userId));
        for (int partnerId : partnerIds) {
            User friend = userService.findById(partnerId);
            chartLabels.add(friend.getEmail());
            chartValues.add(transactionService.getSavings(partnerId));
        }
        model.addAttribute("chartLabels", chartLabels);
        model.addAttribute("chartValues", chartValues);
    }
}
