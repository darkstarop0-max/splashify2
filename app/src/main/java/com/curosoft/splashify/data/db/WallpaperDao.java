package com.curosoft.splashify.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface WallpaperDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertWallpaper(WallpaperEntity entity);

    @Delete
    void deleteWallpaper(WallpaperEntity entity);

    @Query("SELECT * FROM favorites ORDER BY title ASC")
    LiveData<List<WallpaperEntity>> getAllFavorites();
    
    @Query("SELECT * FROM favorites ORDER BY title ASC")
    List<WallpaperEntity> getAllFavoritesSync();

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :id)")
    LiveData<Boolean> isFavorite(String id);
}
