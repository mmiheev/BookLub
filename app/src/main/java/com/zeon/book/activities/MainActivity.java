package com.zeon.book.activities;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.zeon.book.R;
import com.zeon.book.databinding.ActivityMainBinding;

import org.jetbrains.annotations.Contract;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViewBinding();
        setupNavigation();
    }

    private void setupViewBinding() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    private void setupNavigation() {
        NavController navController = getNavController();
        AppBarConfiguration appBarConfiguration = createAppBarConfiguration();

        setupActionBar(navController, appBarConfiguration);
        setupBottomNavigation(navController);
    }

    @NonNull
    private NavController getNavController() {
        return Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
    }

    @NonNull
    @Contract(" -> new")
    private AppBarConfiguration createAppBarConfiguration() {
        return new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_aim,
                R.id.navigation_books,
                R.id.navigation_settings
        ).build();
    }

    private void setupActionBar(NavController navController, AppBarConfiguration appBarConfiguration) {
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
    }

    private void setupBottomNavigation(NavController navController) {
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = getNavController();
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}