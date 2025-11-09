package com.ifmg.a21dayschallangeapp;

import org.json.JSONObject;

public class AuthController {

    public boolean login(String email, String password) {
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", password);

            String response = ApiClient.post("/auth/login", body.toString());
            JSONObject json = new JSONObject(response);
            return json.optBoolean("success", false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean register(String email, String password) {
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", password);

            String response = ApiClient.post("/auth/register", body.toString());
            JSONObject json = new JSONObject(response);
            return json.optBoolean("success", false);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}