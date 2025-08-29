package com.curosoft.splashify.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.curosoft.splashify.databinding.FragmentHomeBinding;
import com.curosoft.splashify.model.Category;
import com.curosoft.splashify.model.WallpaperItem;
import com.curosoft.splashify.R;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initial state: show shimmer, hide lists and states
        showShimmer(true);
        showContent(false);
        showEmpty(false);
        showError(false);

        binding.btnRetry.setOnClickListener(v -> {
            android.widget.Toast.makeText(requireContext(), "Retry clicked", android.widget.Toast.LENGTH_SHORT).show();
        });

        // Categories RecyclerView - horizontal
        List<Category> categories = Arrays.asList(
                new Category("nature", "Nature", R.mipmap.ic_launcher),
                new Category("abstract", "Abstract", R.mipmap.ic_launcher),
                new Category("anime", "Anime", R.mipmap.ic_launcher),
                new Category("space", "Space", R.mipmap.ic_launcher),
                new Category("minimal", "Minimal", R.mipmap.ic_launcher)
        );
        CategoryAdapter categoryAdapter = new CategoryAdapter(categories);
        binding.rvCategories.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvCategories.setAdapter(categoryAdapter);

        // Wallpapers RecyclerView - grid 2 columns
        List<WallpaperItem> wallpapers = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            wallpapers.add(new WallpaperItem(String.valueOf(i), R.mipmap.ic_launcher));
        }
        WallpaperAdapter wallpaperAdapter = new WallpaperAdapter(wallpapers);
        binding.rvWallpapers.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvWallpapers.setAdapter(wallpaperAdapter);

        // Simulate loading delay, then show content
        binding.getRoot().postDelayed(() -> {
            showShimmer(false);
            // Toggle between content/empty/error by commenting lines below as needed for testing
            if (wallpapers.isEmpty()) {
                showEmpty(true);
            } else {
                showContent(true);
            }
            // Example to show error state instead:
            // showError(true);
        }, 2200);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showShimmer(boolean show) {
        binding.shimmerCategories.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.shimmerWallpapers.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) {
            binding.shimmerCategories.startShimmer();
            binding.shimmerWallpapers.startShimmer();
        } else {
            binding.shimmerCategories.stopShimmer();
            binding.shimmerWallpapers.stopShimmer();
        }
    }

    private void showContent(boolean show) {
        binding.rvCategories.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.rvWallpapers.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showEmpty(boolean show) {
        binding.emptyState.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showError(boolean show) {
        binding.errorState.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
