package com.finsync.service;

import com.finsync.model.User;
import com.finsync.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public void register(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }
        String token = UUID.randomUUID().toString();
        User user = new User(email, passwordEncoder.encode(password), "USER");
        user.setVerificationToken(token);
        userRepository.save(user);
        emailService.sendVerificationEmail(email, token);
    }

    public boolean verifyEmail(String token) {
        return userRepository.findByVerificationToken(token).map(user -> {
            user.setVerified(true);
            user.setVerificationToken(null);
            userRepository.save(user);
            return true;
        }).orElse(false);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
    }

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    public void changeRole(Integer userId, String newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        user.setRole(newRole);
        userRepository.save(user);
    }

    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
