package com.curosoft.splashify.model;

import com.google.gson.annotations.SerializedName;

public class TokenResponse {
    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("expires_in")
    public long expiresIn;
}
