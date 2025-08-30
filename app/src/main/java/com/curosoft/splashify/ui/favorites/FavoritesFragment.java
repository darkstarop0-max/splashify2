package com.curosoft.splashify.ui.favorites;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.curosoft.splashify.R;
import com.curosoft.splashify.databinding.FragmentFavoritesBinding;
import com.curosoft.splashify.model.Wallpaper;
import com.curosoft.splashify.viewmodel.FavoritesViewModel;

public class FavoritesFragment extends Fragment {
    private FragmentFavoritesBinding binding;
    private FavoritesViewModel favoritesViewModel;
    private FavoriteWallpaperAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Set up empty state placeholder
        setupEmptyState();
        
        // Initialize view model
        favoritesViewModel = new ViewModelProvider(requireActivity()).get(FavoritesViewModel.class);
        
        // Set up the adapter with our custom FavoriteWallpaperAdapter
        adapter = new FavoriteWallpaperAdapter().setListener(new FavoriteWallpaperAdapter.Listener() {
            @Override
            public void onToggleFavorite(Wallpaper item) {
                boolean isFav = favoritesViewModel.favoriteIds.getValue() != null && 
                                favoritesViewModel.favoriteIds.getValue().contains(item.id);
                if (isFav) favoritesViewModel.remove(item.id);
                else favoritesViewModel.add(item.id, item.title, item.url, null);
            }

            @Override
            public boolean isFavorite(String id) {
                return favoritesViewModel.favoriteIds.getValue() != null && 
                       favoritesViewModel.favoriteIds.getValue().contains(id);
            }
            
            @Override
            public void onDeleteFavorite(Wallpaper item) {
                // Remove from favorites
                favoritesViewModel.remove(item.id);
            }
        });
        
        // Set up RecyclerView
        binding.rvFavorites.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvFavorites.setAdapter(adapter);

        // Observe favorites list changes
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
            
            // Update UI based on list content
            adapter.submitList(list);
            updateEmptyStateVisibility(list.isEmpty());
        });
    }
    
    private void setupEmptyState() {
        View emptyView = binding.emptyState.getRoot();
        ImageView emptyIcon = emptyView.findViewById(R.id.iconPlaceholder);
        TextView emptyText = emptyView.findViewById(R.id.textPlaceholder);
        TextView emptySubtext = emptyView.findViewById(R.id.subtextPlaceholder);

        emptyIcon.setImageResource(R.drawable.ic_favorite_24);
        emptyIcon.setColorFilter(ContextCompat.getColor(requireContext(), R.color.primary));
        emptyText.setText(R.string.no_favorites);
        emptySubtext.setText(R.string.no_favorites_subtitle);
    }
    
    private void updateEmptyStateVisibility(boolean isEmpty) {
        binding.rvFavorites.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        binding.emptyState.getRoot().setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
