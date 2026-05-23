package com.finsync.controller;

import com.finsync.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JavaMailSender mailSender;

    public AuthController(UserService userService, JavaMailSender mailSender) {
        this.userService = userService;
        this.mailSender = mailSender;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String code,
                               HttpSession session,
                               Model model) {

        try {

            String sessionCode = (String) session.getAttribute("regCode");
            String sessionEmail = (String) session.getAttribute("regEmail");

            if (sessionCode == null || sessionEmail == null) {
                model.addAttribute("error", "Сначала запросите код подтверждения");
                return "auth/register";
            }

            if (!sessionEmail.equals(email)) {
                model.addAttribute("error", "Email не совпадает с тем, на который был отправлен код");
                return "auth/register";
            }

            if (!sessionCode.equals(code)) {
                model.addAttribute("error", "Неверный код подтверждения");
                return "auth/register";
            }

            userService.register(email, password);

            session.removeAttribute("regCode");
            session.removeAttribute("regEmail");

            return "redirect:/auth/login?registered";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/verify")
    public String verify(@RequestParam String token) {
        boolean ok = userService.verifyEmail(token);
        return ok ? "redirect:/auth/login?verified" : "redirect:/auth/login?badToken";
    }

    @PostMapping("/send-code")
    @ResponseBody
    public ResponseEntity<String> sendCode(@RequestParam String email, HttpSession session) {
        try {
            String code = String.valueOf((int) ((Math.random() * 900000) + 100000));

            session.setAttribute("regCode", code);
            session.setAttribute("regEmail", email);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Код подтверждения FinSync");
            message.setText("Ваш код для регистрации: " + code);

            mailSender.send(message);

            return ResponseEntity.ok("Код отправлен");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Ошибка при отправке кода");
        }
    }
}