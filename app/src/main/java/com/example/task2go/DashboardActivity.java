package com.example.task2go;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        // Load TaskListFragment when DashboardActivity starts
        loadFragment(new TaskListFragment());

        // Bottom Navigation Setup
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.nav_tasks) {
                selectedFragment = new TaskListFragment();
            } else if (item.getItemId() == R.id.nav_profile) {
                selectedFragment = new Fragment();
            }
            if (selectedFragment != null) {
                loadFragment(selectedFragment);
            }
            return true;
        });

        FloatingActionButton fabCreateTask = findViewById(R.id.fabCreateTask);
        fabCreateTask.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, PostTaskActivity.class);
            startActivity(intent);
        });
    }
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, fragment);
        transaction.commit();
    }
}