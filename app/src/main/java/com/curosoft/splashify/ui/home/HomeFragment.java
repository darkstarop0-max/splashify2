package com.curosoft.splashify.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.navigation.Navigation;

import com.curosoft.splashify.R;
import com.curosoft.splashify.databinding.FragmentHomeBinding;
import com.curosoft.splashify.model.Category;
import com.curosoft.splashify.viewmodel.HomeViewModel;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private WallpaperAdapter wallpaperAdapter;

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

        binding.btnRetry.setOnClickListener(v -> viewModel.loadPopular());

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
        wallpaperAdapter = new WallpaperAdapter();
        binding.rvWallpapers.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvWallpapers.setAdapter(wallpaperAdapter);

        // ViewModel and observers
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        viewModel.loading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) showShimmer(isLoading);
        });
        viewModel.wallpapers.observe(getViewLifecycleOwner(), list -> {
            showShimmer(false);
            if (list == null || list.isEmpty()) {
                showContent(false);
                showEmpty(true);
                showError(false);
            } else {
                wallpaperAdapter.submitList(list);
                showEmpty(false);
                showError(false);
                showContent(true);
            }
        });
        viewModel.error.observe(getViewLifecycleOwner(), err -> {
            if (err != null) {
                showShimmer(false);
                showContent(false);
                showEmpty(false);
                showError(true);
            }
        });

        // Load data
        viewModel.loadPopular();

        // Toolbar menu actions
        binding.topAppBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_search) {
                Navigation.findNavController(view).navigate(R.id.action_home_to_search);
                return true;
            }
            return false;
        });
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
