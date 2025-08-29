package com.curosoft.splashify.repository;

import android.util.Log;

import com.curosoft.splashify.BuildConfig;
import com.curosoft.splashify.model.TokenResponse;
import com.curosoft.splashify.model.Wallpaper;
import com.curosoft.splashify.model.WallpaperResponse;
import com.curosoft.splashify.network.ApiClient;
import com.curosoft.splashify.network.DeviantArtApiService;
import com.curosoft.splashify.network.DeviantArtAuthService;
import com.curosoft.splashify.network.TokenStore;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WallpaperRepository {
    private static final String TAG = "WallpaperRepository";

    private final DeviantArtAuthService authService = ApiClient.getOAuthRetrofit().create(DeviantArtAuthService.class);
    private final DeviantArtApiService apiService = ApiClient.getApiRetrofit().create(DeviantArtApiService.class);

    public interface PopularCallback {
        void onSuccess(List<Wallpaper> wallpapers);
        void onError(Throwable t);
    }

    public void fetchPopular(PopularCallback callback) {
        ensureTokenThen(callback, () -> doFetchPopular(false, callback));
    }

    private void doFetchPopular(boolean hasRetriedAfterToken, PopularCallback callback) {
        String token = TokenStore.getAccessToken();
        String username = BuildConfig.DA_DEFAULT_USERNAME;
        if (username == null || username.trim().isEmpty()) {
            username = "deviantart"; // fallback username for testing
        }
        Log.d(TAG, "Fetching wallpapers via gallery/all for " + username);

        Call<WallpaperResponse> call = apiService.getGalleryAll(username, 20, 0, token);
    call.enqueue(new Callback<WallpaperResponse>() {
            @Override
            public void onResponse(Call<WallpaperResponse> call, Response<WallpaperResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Wallpaper> list = new ArrayList<>();
                    if (response.body().results != null) {
                        for (WallpaperResponse.Deviation d : response.body().results) {
                            String url = d.content != null ? d.content.src : null;
                            Wallpaper w = new Wallpaper();
                            w.id = d.id;
                            w.title = d.title;
                            w.url = url;
                            list.add(w);
                            Log.d(TAG, "Popular: " + d.title + " | " + url);
                        }
                    }
                    callback.onSuccess(list);
                } else if ((response.code() == 401 || response.code() == 403) && !hasRetriedAfterToken) {
                    // Token might be expired; refresh and retry once
                    Log.w(TAG, "401 received. Refreshing token and retrying once.");
                    TokenStore.setToken(null, 0);
                    ensureTokenThen(callback, () -> doFetchPopular(true, callback));
                } else {
                    callback.onError(new Exception("Popular failed: " + response.code() + " " + response.message()));
                }
            }

            @Override
            public void onFailure(Call<WallpaperResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    private void ensureTokenThen(PopularCallback errorCallback, Runnable onReady) {
        if (TokenStore.isValid()) {
            onReady.run();
            return;
        }
    authService.getToken("client_credentials", BuildConfig.DA_CLIENT_ID, BuildConfig.DA_CLIENT_SECRET)
                .enqueue(new Callback<TokenResponse>() {
                    @Override
                    public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            TokenStore.setToken(response.body().accessToken, response.body().expiresIn);
                            onReady.run();
                        } else {
                            String msg = "Token failed: " + response.code() + " " + response.message();
                            Log.e(TAG, msg);
                            errorCallback.onError(new Exception(msg));
                        }
                    }

                    @Override
                    public void onFailure(Call<TokenResponse> call, Throwable t) {
                        Log.e(TAG, "Token error", t);
                        errorCallback.onError(t);
                    }
                });
    }
}
