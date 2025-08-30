package com.curosoft.splashify.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {WallpaperEntity.class}, version = 1, exportSchema = false)
public abstract class WallpaperDatabase extends RoomDatabase {
    public abstract WallpaperDao wallpaperDao();

    private static volatile WallpaperDatabase INSTANCE;

    public static WallpaperDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (WallpaperDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            WallpaperDatabase.class,
                            "splashify.db"
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
