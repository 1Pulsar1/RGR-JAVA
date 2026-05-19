package com.finsync.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to, String token) {
        String link = "http://localhost:8080/auth/verify?token=" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Подтвердите ваш аккаунт FinSync");
        message.setText("Добро пожаловать в FinSync!\n\nДля подтверждения аккаунта перейдите по ссылке:\n" + link);
        mailSender.send(message);
    }
}
