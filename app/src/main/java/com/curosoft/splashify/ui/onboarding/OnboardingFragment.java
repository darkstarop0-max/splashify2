package com.curosoft.splashify.ui.onboarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.curosoft.splashify.R;
import com.curosoft.splashify.databinding.FragmentOnboardingBinding;
import com.curosoft.splashify.model.OnboardingPage;
import com.curosoft.splashify.utils.Prefs;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;

public class OnboardingFragment extends Fragment {

    private FragmentOnboardingBinding binding;
    private OnboardingAdapter adapter;
    private final ViewPager2.OnPageChangeCallback pageCallback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            updateButtons(position);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<OnboardingPage> pages = Arrays.asList(
                new OnboardingPage(R.mipmap.ic_launcher, "Browse Wallpapers", "Discover stunning wallpapers curated for you."),
                new OnboardingPage(R.mipmap.ic_launcher, "Search by Category", "Find the perfect look with smart filters."),
                new OnboardingPage(R.mipmap.ic_launcher, "Save Your Favorites", "Keep your best picks handy for quick access.")
        );

        adapter = new OnboardingAdapter(pages);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.registerOnPageChangeCallback(pageCallback);
        binding.viewPager.setPageTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                page.setAlpha(0.2f + (1 - Math.abs(position)) * 0.8f);
                page.setTranslationX(-position * 50);
            }
        });

    // Setup dot indicators
    initDots(pages.size());
    setDotSelected(0);

        binding.btnSkip.setOnClickListener(v -> binding.viewPager.setCurrentItem(adapter.getItemCount() - 1, true));
        binding.btnNext.setOnClickListener(v -> binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1, true));
        binding.btnGetStarted.setOnClickListener(v -> {
            Prefs.setOnboarded(requireContext(), true);
            NavController navController = NavHostFragment.findNavController(OnboardingFragment.this);
            navController.navigate(R.id.action_onboarding_to_home);
        });

        updateButtons(0);
    }

    private void updateButtons(int position) {
        int last = adapter.getItemCount() - 1;
        boolean isLast = position == last;
        binding.btnNext.setVisibility(isLast ? View.GONE : View.VISIBLE);
        binding.btnGetStarted.setVisibility(isLast ? View.VISIBLE : View.GONE);
        setDotSelected(position);
    }

    private void initDots(int count) {
        binding.dotsContainer.removeAllViews();
        for (int i = 0; i < count; i++) {
            View dot = new View(requireContext());
            int size = (int) (getResources().getDisplayMetrics().density * 8);
            int margin = (int) (getResources().getDisplayMetrics().density * 6);
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(size, size);
            lp.setMargins(margin, 0, margin, 0);
            dot.setLayoutParams(lp);
            dot.setBackgroundResource(R.drawable.dot_inactive);
            binding.dotsContainer.addView(dot);
        }
    }

    private void setDotSelected(int index) {
        int count = binding.dotsContainer.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = binding.dotsContainer.getChildAt(i);
            child.setBackgroundResource(i == index ? R.drawable.dot_active : R.drawable.dot_inactive);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.viewPager.unregisterOnPageChangeCallback(pageCallback);
        binding = null;
    }
}
