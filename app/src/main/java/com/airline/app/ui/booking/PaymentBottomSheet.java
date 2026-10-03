package com.airline.app.ui.booking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airline.app.databinding.BottomSheetPaymentBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Locale;

public class PaymentBottomSheet extends BottomSheetDialogFragment {
    public interface PaymentAuthorizationListener {
        void onPaymentAuthorized(String paymentMethod);
    }

    private BottomSheetPaymentBinding binding;
    private PaymentAuthorizationListener listener;
    private double amount;
    private String paymentMethod;

    public static PaymentBottomSheet newInstance(double amount, String paymentMethod) {
        PaymentBottomSheet sheet = new PaymentBottomSheet();
        Bundle args = new Bundle();
        args.putDouble("amount", amount);
        args.putString("method", paymentMethod);
        sheet.setArguments(args);
        return sheet;
    }

    public void setPaymentAuthorizationListener(PaymentAuthorizationListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetPaymentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            amount = getArguments().getDouble("amount", 0.0);
            paymentMethod = getArguments().getString("method", "CARD");
        }

        binding.tvSheetAmount.setText(String.format(Locale.US, "$%.2f", amount));
        binding.tvSheetSubtitle.setText("Authorizing payment via " + paymentMethod + "...");

        binding.btnConfirmPayment.setOnClickListener(v -> {
            binding.pbSheetLoading.setVisibility(View.VISIBLE);
            binding.btnConfirmPayment.setEnabled(false);
            if (listener != null) {
                listener.onPaymentAuthorized(paymentMethod);
            }
            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
