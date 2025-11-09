package com.ifmg.backend21daysapp.controller;

import com.ifmg.backend21daysapp.model.User;
import com.ifmg.backend21daysapp.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService service;

    public AuthController(UserService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        boolean ok = service.register(user);
        if (!ok) return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Email ja cadastrado"));
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> body) {
        String email = body.get("email");
        String password = body.get("password");
        boolean ok = service.login(email, password);
        if (!ok) return ResponseEntity.status(401).body(Map.of("success", false, "message", "Credenciais inválidas"));
        return ResponseEntity.ok(Map.of("success", true));
    }
}