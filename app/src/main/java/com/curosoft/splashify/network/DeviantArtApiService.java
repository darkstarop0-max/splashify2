package com.curosoft.splashify.network;

import com.curosoft.splashify.model.WallpaperResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface DeviantArtApiService {
    @GET("gallery/all")
    Call<WallpaperResponse> getGalleryAll(
        @Query("username") String username,
        @Query("limit") Integer limit,
        @Query("offset") Integer offset,
        @Query("access_token") String accessToken
    );
}
