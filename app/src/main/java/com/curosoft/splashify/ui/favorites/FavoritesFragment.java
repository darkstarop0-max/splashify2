package com.curosoft.splashify.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.curosoft.splashify.databinding.FragmentFavoritesBinding;
import com.curosoft.splashify.model.Wallpaper;
import com.curosoft.splashify.ui.home.WallpaperAdapter;
import com.curosoft.splashify.viewmodel.FavoritesViewModel;

public class FavoritesFragment extends Fragment {
    private FragmentFavoritesBinding binding;
    private FavoritesViewModel favoritesViewModel;
    private WallpaperAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        favoritesViewModel = new ViewModelProvider(requireActivity()).get(FavoritesViewModel.class);
        adapter = new WallpaperAdapter().setListener(new WallpaperAdapter.Listener() {
            @Override
            public void onToggleFavorite(Wallpaper item) {
                boolean isFav = favoritesViewModel.favoriteIds.getValue() != null && favoritesViewModel.favoriteIds.getValue().contains(item.id);
                if (isFav) favoritesViewModel.remove(item.id);
                else favoritesViewModel.add(item.id, item.title, item.url, null);
            }

            @Override
            public boolean isFavorite(String id) {
                return favoritesViewModel.favoriteIds.getValue() != null && favoritesViewModel.favoriteIds.getValue().contains(id);
            }
        });
        binding.rvFavorites.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvFavorites.setAdapter(adapter);

        favoritesViewModel.favorites.observe(getViewLifecycleOwner(), entities -> {
            java.util.List<Wallpaper> list = new java.util.ArrayList<>();
            if (entities != null) {
                for (com.curosoft.splashify.data.db.WallpaperEntity e : entities) {
                    Wallpaper w = new Wallpaper();
                    w.id = e.id;
                    w.title = e.title;
                    w.url = e.imageUrl;
                    list.add(w);
                }
            }
            adapter.submitList(list);
            binding.emptyState.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
