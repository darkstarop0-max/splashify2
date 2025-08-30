package com.curosoft.splashify.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatDelegate;

import com.curosoft.splashify.databinding.FragmentSettingsBinding;
import com.curosoft.splashify.utils.Prefs;
import com.bumptech.glide.Glide;
import com.curosoft.splashify.BuildConfig;
import android.widget.Toast;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Version display
    binding.tvVersion.setText("Version " + BuildConfig.VERSION_NAME);

        // Dark mode toggle
        int mode = Prefs.getNightMode(requireContext());
        binding.switchDarkMode.setChecked(mode == AppCompatDelegate.MODE_NIGHT_YES);
        binding.switchDarkMode.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            int newMode = isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO;
            Prefs.setNightMode(requireContext(), newMode);
            AppCompatDelegate.setDefaultNightMode(newMode);
        });

        // Clear cache
        binding.rowClearCache.setOnClickListener(v -> {
            try {
                // Clear Glide disk cache off the main thread
                new Thread(() -> Glide.get(requireContext()).clearDiskCache()).start();
                // Clear Glide memory cache on main thread
                Glide.get(requireContext()).clearMemory();
                // Clear app cache dir
                deleteCacheDir(requireContext().getCacheDir());
                Toast.makeText(requireContext(), "Cache cleared", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Failed to clear cache", Toast.LENGTH_SHORT).show();
            }
        });

        // About & Support
        binding.rowAbout.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Splashify is a demo app", Toast.LENGTH_SHORT).show();
        });
        binding.rowSupport.setOnClickListener(v -> {
            android.content.Intent email = new android.content.Intent(android.content.Intent.ACTION_SENDTO);
            email.setData(android.net.Uri.parse("mailto:support@example.com"));
            email.putExtra(android.content.Intent.EXTRA_SUBJECT, "Splashify Support");
            if (email.resolveActivity(requireContext().getPackageManager()) != null) {
                startActivity(email);
            } else {
                Toast.makeText(requireContext(), "No email app found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCacheDir(java.io.File dir) {
        if (dir == null) return;
        if (dir.isDirectory()) {
            java.io.File[] children = dir.listFiles();
            if (children != null) {
                for (java.io.File child : children) deleteCacheDir(child);
            }
        }
        dir.delete();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
