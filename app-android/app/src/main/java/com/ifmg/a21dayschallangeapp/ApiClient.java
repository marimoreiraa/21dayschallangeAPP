package com.ifmg.a21dayschallangeapp;

import android.util.Log;

import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Response;

public class ApiClient {

    private static final String BASE_URL = "http://10.0.2.2:3100/api"; // emulador Android -> localhost

    public static JSONObject post(android.content.Context context, String endpoint, String jsonBody) {

        Log.i("mylog.ApiClient,post", BASE_URL + endpoint);

        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));

            Request.Builder builder = new Request.Builder()
                    .url(BASE_URL + endpoint)
                    .post(body);

            String token = SessionManager.getToken(context);
            if (token != null) {
                builder.addHeader("Authorization", "Bearer " + token);
            }

            Request request = builder.build();

            Response response = client.newCall(request).execute();
            Log.i("mylog.ApiClient.post.response", response.toString());

            String responseBody = response.body().string();
            JSONObject json = new JSONObject(responseBody);
            Log.i("mylog.ApiClient.post.json", json.toString());

            return json;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject get(android.content.Context context, String endpoint) {

        Log.i("mylog.ApiClient.get", BASE_URL + endpoint);

        try {
            OkHttpClient client = new OkHttpClient();

            Request.Builder builder = new Request.Builder()
                    .url(BASE_URL + endpoint)
                    .get();

            String token = SessionManager.getToken(context);
            if (token != null) {
                builder.addHeader("Authorization", "Bearer " + token);
            }

            Request request = builder.build();
            Response response = client.newCall(request).execute();
            Log.i("mylog.ApiClient.get.response", response.toString());
            
            String responseBody = response.body().string();
            JSONObject json = new JSONObject(responseBody);
            Log.i("mylog.ApiClient.get.json", json.toString());

            return json;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}