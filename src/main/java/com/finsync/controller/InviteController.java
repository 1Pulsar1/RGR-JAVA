package com.finsync.controller;

import com.finsync.service.InviteService;
import com.finsync.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/invites")
public class InviteController {

    private final InviteService inviteService;
    private final UserService userService;

    public InviteController(InviteService inviteService, UserService userService) {
        this.inviteService = inviteService;
        this.userService = userService;
    }

    @GetMapping
    public String list(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        int userId = userService.findByEmail(userDetails.getUsername()).getId();
        model.addAttribute("sent", inviteService.getSent(userId));
        model.addAttribute("received", inviteService.getReceived(userId));
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
