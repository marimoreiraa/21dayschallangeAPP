package com.ifmg.a21dayschallangeapp;

import java.util.HashMap;
import java.util.Map;

public class AuthController {

    // Simulação de "banco de dados" local
    private final Map<String, String> users = new HashMap<>();

    public AuthController() {
        // Usuário padrão (você pode mudar depois)
        users.put("admin@gmail.com", "123456");
        users.put("teste@teste.com", "senha");
    }

    public boolean login(String email, String password) {
        return users.containsKey(email) && users.get(email).equals(password);
    }
}