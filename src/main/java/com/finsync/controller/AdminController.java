package com.finsync.controller;

import com.finsync.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public String users(Model model,
                        @AuthenticationPrincipal UserDetails userDetails) {
        model.addAttribute("users", userService.findAll());
        model.addAttribute("currentUserId", userService.findByEmail(userDetails.getUsername()).getId());
        return "admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String changeRole(@PathVariable Integer id, @RequestParam String role,
                             @AuthenticationPrincipal UserDetails userDetails) {
        Integer currentId = userService.findByEmail(userDetails.getUsername()).getId();
        if (currentId.equals(id)) {
            return "redirect:/admin/users?error=self";
        }
        userService.changeRole(id, role);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Integer id,
                             @AuthenticationPrincipal UserDetails userDetails) {
        Integer currentId = userService.findByEmail(userDetails.getUsername()).getId();
        if (currentId.equals(id)) {
            return "redirect:/admin/users?error=self";
        }
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }
}
