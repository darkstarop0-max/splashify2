package com.curosoft.splashify.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {
    private static final String NAME = "splashify_prefs";
    private static final String KEY_ONBOARDED = "onboarded";
    private static final String KEY_NIGHT_MODE = "night_mode"; // AppCompatDelegate mode int

    public static boolean isOnboarded(Context context) {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_ONBOARDED, false);
    }

    public static void setOnboarded(Context context, boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(KEY_ONBOARDED, value);
        editor.apply();
    }

    public static int getNightMode(Context context) {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
                .getInt(KEY_NIGHT_MODE, androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
    }

    public static void setNightMode(Context context, int mode) {
        SharedPreferences.Editor editor = context.getSharedPreferences(NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(KEY_NIGHT_MODE, mode);
        editor.apply();
    }
}
