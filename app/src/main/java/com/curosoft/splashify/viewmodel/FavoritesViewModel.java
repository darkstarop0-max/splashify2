package com.curosoft.splashify.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.curosoft.splashify.data.db.WallpaperEntity;
import com.curosoft.splashify.repository.FavoritesRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FavoritesViewModel extends AndroidViewModel {
    private final FavoritesRepository repo;
    public final LiveData<List<WallpaperEntity>> favorites;
    public final MediatorLiveData<Set<String>> favoriteIds = new MediatorLiveData<>();

    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        repo = new FavoritesRepository(application);
        favorites = repo.getAllFavorites();
        favoriteIds.addSource(favorites, list -> {
            Set<String> ids = new HashSet<>();
            if (list != null) {
                for (WallpaperEntity e : list) ids.add(e.id);
            }
            favoriteIds.postValue(ids);
        });
    }

    public void add(String id, String title, String url, String author) {
        WallpaperEntity e = new WallpaperEntity();
        e.id = id;
        e.title = title;
        e.imageUrl = url;
        e.author = author;
        repo.addToFavorites(e);
    }

    public void remove(String id) {
        WallpaperEntity e = new WallpaperEntity();
        e.id = id;
        repo.removeFromFavorites(e);
    }
}
