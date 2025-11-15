package com.ifmg.a21dayschallangeapp;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class ApiClient {

    private static final String BASE_URL = "http://172.20.10.3:8080/api"; // emulador Android -> localhost

    public static String post(String endpoint, String jsonBody) {
        try {
            URL url = new URL(BASE_URL + endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes("utf-8"));
            }

            Scanner sc = new Scanner(conn.getInputStream());
            StringBuilder response = new StringBuilder();
            while (sc.hasNext()) {
                response.append(sc.nextLine());
            }
            sc.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\": false, \"error\": \"" + e.getMessage() + "\"}";
        }
    }
}
