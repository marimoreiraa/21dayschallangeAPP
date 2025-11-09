package com.ifmg.backend21daysapp.service;

import com.ifmg.backend21daysapp.model.User;
import com.ifmg.backend21daysapp.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repo, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean register(User user) {
        if (repo.findByEmail(user.getEmail()).isPresent()) {
            return false; // Email já existe
        }
        // hash da senha antes de salvar
        String raw = user.getPassword();
        String hashed = passwordEncoder.encode(raw);
        user.setPassword(hashed);
        repo.save(user);
        return true;
    }

    public boolean login(String email, String rawPassword) {
        Optional<User> userOpt = repo.findByEmail(email);
        if (userOpt.isEmpty()) return false;
        User user = userOpt.get();
        // compara senha crua com hash
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    // utilitário para forçar reset / re-hash se necessário
    public void rehashAllUsersIfNeeded() {
        repo.findAll().forEach(u -> {
            String pw = u.getPassword();
            // se não parecer bcrypt (não começa com $2a$ ou $2b$), re-hash
            if (pw != null && !pw.startsWith("$2a$") && !pw.startsWith("$2b$")) {
                u.setPassword(passwordEncoder.encode(pw));
                repo.save(u);
            }
        });
    }
}