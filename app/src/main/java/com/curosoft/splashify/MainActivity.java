package com.curosoft.splashify;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.curosoft.splashify.navigation.NavGraphBuilder;
import com.curosoft.splashify.repository.WallpaperRepository;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavGraphBuilder.setup(navController, this);
        }

        bottomNav = findViewById(R.id.bottomNav);
        if (bottomNav != null && navController != null) {
            NavigationUI.setupWithNavController(bottomNav, navController);
        }

    }

    @Override
    public void onBackPressed() {
        if (bottomNav != null && navController != null) {
            int current = navController.getCurrentDestination() != null ? navController.getCurrentDestination().getId() : 0;
            if (current != 0 && current != R.id.homeFragment) {
                bottomNav.setSelectedItemId(R.id.homeFragment);
                navController.navigate(R.id.homeFragment);
                return;
            }
        }
        super.onBackPressed();
    }
}