package com.curosoft.splashify.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.appcompat.widget.SearchView;

import com.curosoft.splashify.databinding.FragmentSearchBinding;
import com.curosoft.splashify.ui.home.WallpaperAdapter;
import com.curosoft.splashify.viewmodel.SearchViewModel;
import com.curosoft.splashify.viewmodel.FavoritesViewModel;

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

        // Setup SearchView in toolbar
        MenuItem item = binding.searchToolbar.getMenu().findItem(com.curosoft.splashify.R.id.action_search_view);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search tags...");
        searchView.setIconified(false);
        item.expandActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        binding.emptyState.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showError(boolean show) {
        binding.errorState.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
