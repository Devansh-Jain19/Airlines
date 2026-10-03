package com.airline.app.ui.search;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.airline.app.R;
import com.airline.app.databinding.ActivityMainBinding;
import com.airline.app.ui.auth.LoginActivity;
import com.airline.app.ui.ticket.MyBookingsActivity;
import com.airline.app.util.SessionManager;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Default screen is SearchFragment
        if (savedInstanceState == null) {
            loadFragment(new SearchFragment());
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_search) {
                loadFragment(new SearchFragment());
                return true;
            } else if (itemId == R.id.nav_bookings) {
                if (sessionManager.isLoggedIn()) {
                    startActivity(new Intent(MainActivity.this, MyBookingsActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
                return false;
            } else if (itemId == R.id.nav_profile) {
                if (!sessionManager.isLoggedIn()) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, MyBookingsActivity.class));
                }
                return false;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit();
    }
}
