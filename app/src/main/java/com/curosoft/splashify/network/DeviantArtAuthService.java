package com.curosoft.splashify.network;

import com.curosoft.splashify.model.TokenResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface DeviantArtAuthService {
    @FormUrlEncoded
    @POST("oauth2/token")
    Call<TokenResponse> getToken(
            @Field("grant_type") String grantType,
            @Field("client_id") String clientId,
            @Field("client_secret") String clientSecret
    );
}
