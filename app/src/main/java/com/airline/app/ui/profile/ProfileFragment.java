package com.airline.app.ui.profile;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.airline.app.R;
import com.airline.app.databinding.FragmentProfileBinding;
import com.airline.app.ui.auth.LoginActivity;
import com.airline.app.ui.auth.RegisterActivity;
import com.airline.app.ui.ticket.MyBookingsActivity;
import com.airline.app.util.SessionManager;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(requireContext());

        setupListeners();
        renderProfile();
    }

    @Override
    public void onResume() {
        super.onResume();
        renderProfile();
    }

    private void setupListeners() {
        // Guest Actions
        binding.btnGuestSignIn.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), LoginActivity.class));
        });

        binding.btnGuestRegister.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), RegisterActivity.class));
        });

        // Shortcut to Bookings & Tickets
        binding.btnViewBookings.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), MyBookingsActivity.class));
        });

        // Sign Out with Confirmation Dialog
        binding.btnSignOut.setOnClickListener(v -> showSignOutDialog());
    }

    private void renderProfile() {
        boolean isDark = sessionManager.isDarkMode();

        // Prevent listener feedback loop during view recreation
        binding.switchDarkMode.setOnCheckedChangeListener(null);
        binding.switchDarkModeGuest.setOnCheckedChangeListener(null);

        binding.switchDarkMode.setChecked(isDark);
        binding.switchDarkModeGuest.setChecked(isDark);

        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed() && sessionManager.isDarkMode() != isChecked) {
                sessionManager.setDarkMode(isChecked);
            }
        });

        binding.switchDarkModeGuest.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed() && sessionManager.isDarkMode() != isChecked) {
                sessionManager.setDarkMode(isChecked);
            }
        });

        if (!sessionManager.isLoggedIn()) {
            binding.layoutLoggedInContent.setVisibility(View.GONE);
            binding.layoutLoggedOutContent.setVisibility(View.VISIBLE);
            return;
        }

        binding.layoutLoggedInContent.setVisibility(View.VISIBLE);
        binding.layoutLoggedOutContent.setVisibility(View.GONE);

        // Retrieve user details filled during registration or login
        String fullName = sessionManager.getName();
        String email = sessionManager.getEmail();
        String phone = sessionManager.getPhone();
        String passport = sessionManager.getPassportNumber();
        long passengerId = sessionManager.getPassengerId();

        // Header Section
        binding.tvProfileHeaderName.setText(fullName != null && !fullName.isEmpty() ? fullName : "Valued Passenger");
        binding.tvProfileHeaderEmail.setText(email != null && !email.isEmpty() ? email : "No email registered");

        // Initials for Avatar
        String initials = computeInitials(fullName);
        binding.tvProfileAvatarInitials.setText(initials);

        // Passenger ID tag
        if (passengerId > 0) {
            binding.tvPassengerIdTag.setText("ID #" + passengerId);
        } else {
            binding.tvPassengerIdTag.setText("MEMBER");
        }

        // Personal Details Card (what the user filled)
        binding.tvDetailFullName.setText(fullName != null && !fullName.isEmpty() ? fullName : "—");
        binding.tvDetailEmail.setText(email != null && !email.isEmpty() ? email : "—");
        binding.tvDetailPhone.setText(phone != null && !phone.isEmpty() ? phone : "Not Provided");
        binding.tvDetailPassport.setText(passport != null && !passport.isEmpty() ? passport : "Not Provided");
    }

    private String computeInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "US";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        } else if (parts.length >= 2) {
            String first = parts[0].substring(0, 1).toUpperCase();
            String second = parts[parts.length - 1].substring(0, 1).toUpperCase();
            return first + second;
        }
        return "US";
    }

    private void showSignOutDialog() {
        new AlertDialog.Builder(requireContext())
            .setTitle(R.string.logout_confirm_title)
            .setMessage(R.string.logout_confirm_msg)
            .setPositiveButton("Sign Out", (dialog, which) -> {
                sessionManager.logout();
                Toast.makeText(requireContext(), "You have been signed out.", Toast.LENGTH_SHORT).show();
                renderProfile();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
