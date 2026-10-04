package com.airline.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.airline.app.databinding.ActivityRegisterBinding;
import com.airline.app.model.request.RegisterRequest;
import com.airline.app.model.response.PassengerDto;
import com.airline.app.network.Resource;
import com.airline.app.util.SessionManager;
import com.airline.app.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private AuthViewModel authViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.btnRegister.setOnClickListener(v -> handleRegister());

        binding.tvGoToLogin.setOnClickListener(v -> finish());

        // Observe Register Result
        authViewModel.getRegisterResult().observe(this, resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.btnRegister.setEnabled(false);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnRegister.setEnabled(true);
                    if (resource.data != null) {
                        PassengerDto p = resource.data;
                        String fName = binding.etFirstName.getText() != null ? binding.etFirstName.getText().toString().trim() : "";
                        String lName = binding.etLastName.getText() != null ? binding.etLastName.getText().toString().trim() : "";
                        String ph = binding.etPhone.getText() != null ? binding.etPhone.getText().toString().trim() : "";
                        String pass = binding.etPassport.getText() != null ? binding.etPassport.getText().toString().trim() : "";
                        if (p.getFirstName() == null || p.getFirstName().isEmpty()) p.setFirstName(fName);
                        if (p.getLastName() == null || p.getLastName().isEmpty()) p.setLastName(lName);
                        if (p.getPhone() == null || p.getPhone().isEmpty()) p.setPhone(ph);
                        if (p.getPassportNumber() == null || p.getPassportNumber().isEmpty()) p.setPassportNumber(pass);
                        sessionManager.savePassenger(p);
                        Toast.makeText(this, "Registration successful! Please log in.", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnRegister.setEnabled(true);
                    Toast.makeText(this, resource.message != null ? resource.message : "Registration failed", Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void handleRegister() {
        String firstName = binding.etFirstName.getText() != null ? binding.etFirstName.getText().toString().trim() : "";
        String lastName = binding.etLastName.getText() != null ? binding.etLastName.getText().toString().trim() : "";
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String password = binding.etPassword.getText() != null ? binding.etPassword.getText().toString().trim() : "";
        String phone = binding.etPhone.getText() != null ? binding.etPhone.getText().toString().trim() : "";
        String passport = binding.etPassport.getText() != null ? binding.etPassport.getText().toString().trim() : "";

        if (firstName.isEmpty()) {
            binding.tilFirstName.setError("First name is required");
            return;
        } else {
            binding.tilFirstName.setError(null);
        }

        if (email.isEmpty()) {
            binding.tilEmail.setError("Email is required");
            return;
        } else {
            binding.tilEmail.setError(null);
        }

        if (password.length() < 6) {
            binding.tilPassword.setError("Password must be at least 6 characters");
            return;
        } else {
            binding.tilPassword.setError(null);
        }

        RegisterRequest request = new RegisterRequest(firstName, lastName, email, password, phone, passport);
        authViewModel.register(request);
    }
}
