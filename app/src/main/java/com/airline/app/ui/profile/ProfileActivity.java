package com.airline.app.ui.profile;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.airline.app.R;
import com.airline.app.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.toolbar.setNavigationOnClickListener(v -> finish());

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.profileContainer, new ProfileFragment())
                .commit();
        }
    }
}
