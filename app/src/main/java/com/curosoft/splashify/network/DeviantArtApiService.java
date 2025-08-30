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

    // Search by tag. Example:
    // GET https://www.deviantart.com/api/v1/oauth2/browse/tags?tag={query}&limit=30&access_token={token}
    @GET("browse/tags")
    Call<WallpaperResponse> getBrowseTags(
        @Query("tag") String tag,
        @Query("limit") Integer limit,
        @Query("access_token") String accessToken
    );
}
