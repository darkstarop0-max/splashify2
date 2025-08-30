package com.curosoft.splashify.data.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorites")
public class WallpaperEntity {
    @PrimaryKey
    @NonNull
    public String id;
    public String title;
    public String imageUrl;
    public String author; // optional
}
