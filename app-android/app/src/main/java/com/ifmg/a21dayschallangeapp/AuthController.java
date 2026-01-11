package com.ifmg.a21dayschallangeapp;

import android.util.Log;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class AuthController {

    public boolean login(android.content.Context context, String email, String password) {
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", hashPassword(password));

            JSONObject response = ApiClient.post(context,"/auth/login", body.toString());
            if (response.optInt("code") == 200 && response.has("token")) {
                String jwtToken = response.getString("token");
                SessionManager.saveToken(context, jwtToken); // Usaremos o TokenManager
                return true;
            }

            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean register(android.content.Context context, String name, String email, String password, String resposta) {
        try {
            JSONObject body = new JSONObject();
            body.put("username", name);
            body.put("email", email);
            body.put("password", hashPassword(password));
            body.put("recoveryAnswer", hashPassword(resposta.toLowerCase().trim()));

            JSONObject response = ApiClient.post(context, "/auth/register", body.toString());
            return (response != null && response.getInt("code") == 200);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean recoverPassword(android.content.Context context, String email, String resposta, String novaSenha) {
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("recoveryAnswer", hashPassword(resposta.toLowerCase().trim()));
            body.put("newPassword", hashPassword(novaSenha));

            JSONObject response = ApiClient.post(context, "/auth/recover", body.toString());

            if (response == null) return false;

            return (response.optInt("code") == 200);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String hashPassword(String password){

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            StringBuilder hex = new StringBuilder();
            for (byte b : md.digest(password.getBytes(StandardCharsets.UTF_8))){
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return "";
    }

}