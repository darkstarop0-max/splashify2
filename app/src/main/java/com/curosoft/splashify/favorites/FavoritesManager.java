package com.curosoft.splashify.favorites;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.curosoft.splashify.data.db.WallpaperEntity;
import com.curosoft.splashify.repository.FavoritesRepository;

import java.util.List;

public class FavoritesManager {
    private static FavoritesRepository repo;

    public static synchronized void init(Context context) {
        if (repo == null) repo = new FavoritesRepository(context.getApplicationContext());
    }

    public static LiveData<List<WallpaperEntity>> favorites(Context context) {
        init(context);
        return repo.getAllFavorites();
    }

    public static void toggle(Context context, String id, String title, String url, String author, boolean isFav) {
        init(context);
        if (isFav) {
            WallpaperEntity e = new WallpaperEntity(); e.id = id; repo.removeFromFavorites(e);
        } else {
            WallpaperEntity e = new WallpaperEntity(); e.id = id; e.title = title; e.imageUrl = url; e.author = author; repo.addToFavorites(e);
        }
    }
}
