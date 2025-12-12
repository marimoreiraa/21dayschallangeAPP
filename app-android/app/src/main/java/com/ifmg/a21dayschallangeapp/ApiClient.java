package com.ifmg.a21dayschallangeapp;

import android.util.Log;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.MediaType;
import okhttp3.Response;

public class ApiClient {

    private static final String BASE_URL = "http://192.168.0.160:3100/api"; // emulador Android -> localhost

    public static JSONObject post(String endpoint, String jsonBody) {

        Log.i("mylog.ApiClient,post", BASE_URL + endpoint);

        try {
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(jsonBody, MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(BASE_URL + endpoint)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            Log.i("mylog.ApiClient.post.response", response.toString());

            JSONObject json = new JSONObject(response.body().string());
            Log.i("mylog.ApiClient.post.json", json.toString());

            return json;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
