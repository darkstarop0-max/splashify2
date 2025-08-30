package com.curosoft.splashify.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.curosoft.splashify.R;
import com.curosoft.splashify.databinding.FragmentSearchBinding;
import com.curosoft.splashify.ui.home.WallpaperAdapter;
import com.curosoft.splashify.viewmodel.FavoritesViewModel;
import com.curosoft.splashify.viewmodel.SearchViewModel;

public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private SearchViewModel viewModel;
    private WallpaperAdapter adapter;
    private FavoritesViewModel favoritesViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up back navigation
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToHome();
            }
        });

        favoritesViewModel = new ViewModelProvider(requireActivity()).get(FavoritesViewModel.class);
        adapter = new WallpaperAdapter().setListener(new WallpaperAdapter.Listener() {
            @Override
            public void onToggleFavorite(com.curosoft.splashify.model.Wallpaper item) {
                boolean isFav = favoritesViewModel.favoriteIds.getValue() != null && favoritesViewModel.favoriteIds.getValue().contains(item.id);
                if (isFav) favoritesViewModel.remove(item.id);
                else favoritesViewModel.add(item.id, item.title, item.url, null);
            }

            @Override
            public boolean isFavorite(String id) {
                return favoritesViewModel.favoriteIds.getValue() != null && favoritesViewModel.favoriteIds.getValue().contains(id);
            }
        });
        binding.rvResults.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.rvResults.setAdapter(adapter);

        // Set up placeholder states
        setupPlaceholderViews();

        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        viewModel.loading.observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) showShimmer(isLoading);
        });
        viewModel.results.observe(getViewLifecycleOwner(), list -> {
            showShimmer(false);
            if (list == null || list.isEmpty()) {
                showContent(false);
                showEmpty(true);
                showError(false);
            } else {
                adapter.submitList(list);
                showEmpty(false);
                showError(false);
                showContent(true);
            }
        });
        favoritesViewModel.favoriteIds.observe(getViewLifecycleOwner(), ids -> adapter.notifyDataSetChanged());
        viewModel.error.observe(getViewLifecycleOwner(), err -> {
            if (err != null) {
                showShimmer(false);
                showContent(false);
                showEmpty(false);
                showError(true);
            }
        });

        // Setup SearchView with custom back button behavior
        binding.searchView.setQueryHint(getString(R.string.search_hint));
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Override close button to navigate back to home
        binding.searchView.setOnCloseListener(() -> {
            navigateToHome();
            return true;
        });

        // Find the close button and set our custom listener
        ImageView closeButton = binding.searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        if (closeButton != null) {
            closeButton.setOnClickListener(v -> {
                // Clear query first
                binding.searchView.setQuery("", false);
                // Navigate back to home
                navigateToHome();
            });
        }

        // Find the back button and customize it
        ImageView backButton = binding.searchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        if (backButton != null) {
            backButton.setOnClickListener(v -> navigateToHome());
        }
    }

    private void setupPlaceholderViews() {
        // Empty state
        View emptyView = binding.emptyState.getRoot();
        ImageView emptyIcon = emptyView.findViewById(R.id.iconPlaceholder);
        TextView emptyText = emptyView.findViewById(R.id.textPlaceholder);
        TextView emptySubtext = emptyView.findViewById(R.id.subtextPlaceholder);

        emptyIcon.setImageResource(R.drawable.ic_search_24);
        emptyText.setText(R.string.no_results);
        emptySubtext.setText("Try different keywords or browse popular wallpapers");

        // Error state
        View errorView = binding.errorState.getRoot();
        ImageView errorIcon = errorView.findViewById(R.id.iconPlaceholder);
        TextView errorText = errorView.findViewById(R.id.textPlaceholder);
        TextView errorSubtext = errorView.findViewById(R.id.subtextPlaceholder);

        errorIcon.setImageResource(android.R.drawable.ic_dialog_alert);
        errorIcon.setColorFilter(ContextCompat.getColor(requireContext(), android.R.color.holo_red_light));
        errorText.setText(R.string.error_search);
        errorSubtext.setText("Please check your connection and try again");
    }

    private void navigateToHome() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.homeFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void performSearch(String query) {
        showError(false);
        showEmpty(false);
        viewModel.search(query);
    }

    private void showShimmer(boolean show) {
        binding.shimmerResults.setVisibility(show ? View.VISIBLE : View.GONE);
        if (show) binding.shimmerResults.startShimmer();
        else binding.shimmerResults.stopShimmer();
    }

    private void showContent(boolean show) {
        binding.rvResults.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showEmpty(boolean show) {
        binding.emptyState.getRoot().setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showError(boolean show) {
        binding.errorState.getRoot().setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
