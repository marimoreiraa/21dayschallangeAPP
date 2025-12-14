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
                // **PASSO CRUCIAL: Salvar o token JWT**
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
    public boolean register(android.content.Context context, String name, String email, String password) {
        try {
            JSONObject body = new JSONObject();
            body.put("username",name);
            body.put("email", email);
            body.put("password", hashPassword(password));

            JSONObject response = ApiClient.post(context, "/auth/register", body.toString());

            Log.i("mylog.AuthController.register", body.toString());

            if (response == null) {
                Log.e("mylog.AuthController.register", "Resposta do servidor nula.");
                return false;
            }

            Log.i("mylog.AuthController.register", response.toString(2));

            return (response.getInt("code") == 200);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean requestPasswordReset(android.content.Context context, String email) {
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);

            JSONObject response = ApiClient.post(context, "/auth/reset-password-request", body.toString());

            if (response == null) return false;

            return response.optBoolean("success", false);

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