package com.curosoft.splashify.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.curosoft.splashify.model.Wallpaper;
import com.curosoft.splashify.repository.WallpaperRepository;

import java.util.List;

public class SearchViewModel extends ViewModel {
    private final WallpaperRepository repository = new WallpaperRepository();

    private final MutableLiveData<Boolean> _loading = new MutableLiveData<>(false);
    private final MutableLiveData<List<Wallpaper>> _results = new MutableLiveData<>();
    private final MutableLiveData<String> _error = new MutableLiveData<>();

    public LiveData<Boolean> loading = _loading;
    public LiveData<List<Wallpaper>> results = _results;
    public LiveData<String> error = _error;

    public void search(String query) {
        _error.postValue(null);
        _loading.postValue(true);
        repository.searchByTag(query, new WallpaperRepository.SearchCallback() {
            @Override
            public void onSuccess(List<Wallpaper> wallpapers) {
                _loading.postValue(false);
                _results.postValue(wallpapers);
            }

            @Override
            public void onError(Throwable t) {
                _loading.postValue(false);
                _error.postValue(t.getMessage());
            }
        });
    }
}
