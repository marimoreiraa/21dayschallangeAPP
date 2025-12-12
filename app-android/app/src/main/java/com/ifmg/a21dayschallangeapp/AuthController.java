package com.ifmg.a21dayschallangeapp;

import android.util.Log;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class AuthController {

    public boolean login(String email, String password) {
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", hashPassword(password));

            JSONObject response = ApiClient.post("/auth/login", body.toString());
            return (response.getInt("code") == 200);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean register(String name, String email, String password) {
        try {
            JSONObject body = new JSONObject();
            body.put("username",name);
            body.put("email", email);
            body.put("password", hashPassword(password));

            JSONObject response = ApiClient.post("/auth/register", body.toString());

            Log.i("mylog.AuthController.register", body.toString());
            Log.i("mylog.AuthController.register", response.toString(2));

            return (response.getInt("code") == 200);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean requestPasswordReset(String email) {
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);

            JSONObject response = ApiClient.post("/auth/reset-password-request", body.toString());
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