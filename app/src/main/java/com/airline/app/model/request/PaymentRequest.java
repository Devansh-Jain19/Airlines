package com.airline.app.model.request;

import com.google.gson.annotations.SerializedName;

public class PaymentRequest {
    @SerializedName("bookingId")
    private Long bookingId;

    @SerializedName("amount")
    private Double amount;

    @SerializedName("paymentMethod")
    private String paymentMethod; // CARD, UPI, NET_BANKING, WALLET

    @SerializedName("transactionReference")
    private String transactionReference;

    public PaymentRequest() {}

    public PaymentRequest(Long bookingId, Double amount, String paymentMethod, String transactionReference) {
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.transactionReference = transactionReference;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }
}
