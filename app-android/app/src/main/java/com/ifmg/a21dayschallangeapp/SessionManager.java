package com.ifmg.a21dayschallangeapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "AuthPrefs";
    private static final String KEY_TOKEN = "jwt_token";

    public static void saveToken(Context context, String token) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public static String getToken(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPrefs.getString(KEY_TOKEN, null);
    }

    public static void clearToken(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.remove(KEY_TOKEN);
        editor.apply();
    }
}
