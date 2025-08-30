package com.curosoft.splashify.ui.onboarding;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.curosoft.splashify.R;
import com.curosoft.splashify.databinding.FragmentOnboardingBinding;
import com.curosoft.splashify.model.OnboardingPage;
import com.curosoft.splashify.utils.Prefs;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // We'll set the theme in onViewCreated instead, when we're sure the fragment is attached
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOnboardingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Apply theme and UI setup safely with a post to ensure fragment is attached
        view.post(() -> {
            if (isAdded() && getActivity() != null) {
                try {
                    // Apply the onboarding theme
                    getActivity().setTheme(R.style.Theme_Splashify_Onboarding);
                    
                    // Make the fragment truly full screen with immersive mode
                    View decorView = getActivity().getWindow().getDecorView();
                    decorView.setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN);
                    
                    // Set window flags for edge-to-edge display
                    getActivity().getWindow().setFlags(
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    );
                    
                    // Additional step: ensure we don't apply system window insets
                    ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
                        // Don't apply any insets - return unchanged insets
                        return insets;
                    });
                } catch (Exception e) {
                    Log.e("OnboardingFragment", "Error setting up immersive mode", e);
                }
            }
        });

        List<OnboardingPage> pages = Arrays.asList(
                new OnboardingPage(R.drawable.ic_wallpaper_illustration, "Browse Wallpapers", "Discover stunning wallpapers from talented artists around the world, curated just for you."),
                new OnboardingPage(R.drawable.ic_search_24, "Search by Category", "Find the perfect look with smart filters and categories for any mood or occasion."),
                new OnboardingPage(R.drawable.ic_favorite_24, "Save Your Favorites", "Keep your best picks in one place for quick access and set them with just one tap.")
        );

        adapter = new OnboardingAdapter(pages);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.registerOnPageChangeCallback(pageCallback);
        
        // Set page transformer for smooth transitions
        binding.viewPager.setPageTransformer((page, position) -> {
            float absPosition = Math.abs(position);
            
            // Fade effect
            page.setAlpha(1.0f - (absPosition * 0.5f));
            
            // Scale effect
            float scale = 1.0f - (absPosition * 0.15f);
            page.setScaleX(scale);
            page.setScaleY(scale);
        });

        // Setup dot indicators
        initDots(pages.size());
        setDotSelected(0);

        binding.btnSkip.setOnClickListener(v -> binding.viewPager.setCurrentItem(adapter.getItemCount() - 1, true));
        
        binding.btnNext.setOnClickListener(v -> {
            int current = binding.viewPager.getCurrentItem();
            binding.viewPager.setCurrentItem(current + 1, true);
        });
        
        binding.btnGetStarted.setOnClickListener(v -> {
            Prefs.setOnboarded(requireContext(), true);
            NavController navController = NavHostFragment.findNavController(OnboardingFragment.this);
            navController.navigate(R.id.action_onboarding_to_home);
        });

        updateButtons(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        
        // Only proceed if we have a view and we're attached to an activity
        if (getView() != null) {
            // Use post() to delay execution until the view is fully attached
            getView().post(() -> {
                if (isAdded() && getActivity() != null) {
                    // Ensure the bottom navigation is hidden
                    hideNavigationBar();
                    
                    try {
                        // Set a listener to periodically check and hide the navigation bar
                        // This helps with cases where the user might swipe up and temporarily show the navigation
                        final View decorView = getActivity().getWindow().getDecorView();
                        decorView.setOnSystemUiVisibilityChangeListener(visibility -> {
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0 && isAdded() && getActivity() != null) {
                                // The system bars are visible - hide them again
                                hideNavigationBar();
                            }
                        });
                    } catch (Exception e) {
                        Log.e("OnboardingFragment", "Error setting UI visibility listener", e);
                    }
                }
            });
        }
    }
    
    private void hideNavigationBar() {
        // First check if fragment is attached to an activity
        if (!isAdded() || getActivity() == null) {
            return;
        }
        
        try {
            // Make sure all system UI elements are hidden - use getActivity() instead of requireActivity()
            View decorView = getActivity().getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_FULLSCREEN);
                    
            // Double-check that the bottom navigation in MainActivity is hidden
            View bottomNav = getActivity().findViewById(R.id.bottomNav);
            if (bottomNav != null) {
                bottomNav.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Log.e("OnboardingFragment", "Error in hideNavigationBar", e);
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        // Only perform activity-related cleanup if we're still attached
        if (isAdded() && getActivity() != null) {
            try {
                // Reset window flags when leaving the fragment
                getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
                
                // Reset system UI visibility
                View decorView = getActivity().getWindow().getDecorView();
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                
                // Reset theme back to default app theme
                getActivity().setTheme(R.style.Theme_Splashify);
            } catch (Exception e) {
                Log.e("OnboardingFragment", "Error in onDestroyView", e);
            }
        }
        
        // Always unregister callbacks and clear references
        if (binding != null) {
            binding.viewPager.unregisterOnPageChangeCallback(pageCallback);
            binding = null;
        }
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
            int size = (int) (getResources().getDisplayMetrics().density * 10);
            int margin = (int) (getResources().getDisplayMetrics().density * 8);
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
            
            // Scale animation for selected dot
            if (i == index) {
                child.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200);
            } else {
                child.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200);
            }
        }
    }
}
