package com.curosoft.splashify.network;

public class TokenStore {
    private static String accessToken;
    private static long expiryEpochSeconds;

    public static synchronized void setToken(String token, long expiresInSeconds) {
        accessToken = token;
        long now = System.currentTimeMillis() / 1000L;
        // subtract a small buffer
        if (token == null) {
            expiryEpochSeconds = 0;
        } else {
            expiryEpochSeconds = now + Math.max(0, expiresInSeconds - 60);
        }
    }

    public static synchronized String getAccessToken() {
        return accessToken;
    }

    public static synchronized boolean isValid() {
        if (accessToken == null) return false;
        long now = System.currentTimeMillis() / 1000L;
        return now < expiryEpochSeconds;
    }
}
