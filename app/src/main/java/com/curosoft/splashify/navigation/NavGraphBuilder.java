package com.curosoft.splashify.navigation;

import android.content.Context;

import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;

import com.curosoft.splashify.R;
import com.curosoft.splashify.utils.Prefs;

public class NavGraphBuilder {
    public static void setup(NavController navController, Context context) {
        NavInflater inflater = navController.getNavInflater();
        NavGraph graph = inflater.inflate(R.navigation.nav_graph);

        if (Prefs.isOnboarded(context)) {
            graph.setStartDestination(R.id.homeFragment);
        } else {
            graph.setStartDestination(R.id.onboardingFragment);
        }

        navController.setGraph(graph, null);
    }
}
