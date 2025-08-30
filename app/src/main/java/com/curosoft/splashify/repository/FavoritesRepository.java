package com.curosoft.splashify.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.curosoft.splashify.data.db.WallpaperDao;
import com.curosoft.splashify.data.db.WallpaperDatabase;
import com.curosoft.splashify.data.db.WallpaperEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoritesRepository {
    private final WallpaperDao dao;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    public FavoritesRepository(Context context) {
        dao = WallpaperDatabase.getInstance(context).wallpaperDao();
    }

    public LiveData<List<WallpaperEntity>> getAllFavorites() {
        return dao.getAllFavorites();
    }
    
    public List<WallpaperEntity> getAllFavoritesSync() {
        // This is a simplified implementation that returns an empty list if there's an error
        try {
            return dao.getAllFavoritesSync();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public LiveData<Boolean> isFavorite(String id) {
        return dao.isFavorite(id);
    }

    public void addToFavorites(WallpaperEntity entity) {
        io.execute(() -> dao.insertWallpaper(entity));
    }

    public void removeFromFavorites(WallpaperEntity entity) {
        io.execute(() -> dao.deleteWallpaper(entity));
    }
}
