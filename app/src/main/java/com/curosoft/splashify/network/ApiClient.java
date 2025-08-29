package com.curosoft.splashify.network;

import com.curosoft.splashify.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit oauthRetrofit;
    private static Retrofit apiRetrofit;

    public static Retrofit getOAuthRetrofit() {
        if (oauthRetrofit == null) {
            oauthRetrofit = new Retrofit.Builder()
                    .baseUrl("https://www.deviantart.com/")
                    .client(baseClient(false))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return oauthRetrofit;
    }

    public static Retrofit getApiRetrofit() {
        if (apiRetrofit == null) {
            apiRetrofit = new Retrofit.Builder()
                    .baseUrl("https://www.deviantart.com/api/v1/oauth2/")
                    .client(baseClient(false))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return apiRetrofit;
    }

    private static OkHttpClient baseClient(boolean withAuth) {
        OkHttpClient.Builder b = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        b.addInterceptor(logging);

    // no implicit auth header; we pass the token explicitly as a query param where required
        return b.build();
    }
}
