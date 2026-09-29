package com.airline.app.model.response;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class PaymentResponseDto implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("bookingId")
    private Long bookingId;

    @SerializedName("paymentMethod")
    private String paymentMethod;

    @SerializedName("amount")
    private Double amount;

    @SerializedName("status")
    private String status; // SUCCESS, FAILED, PENDING

    @SerializedName("transactionId")
    private String transactionId;

    @SerializedName("paymentTime")
    private String paymentTime;

    public PaymentResponseDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Double getAmount() {
        return amount != null ? amount : 0.0;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status != null ? status : "SUCCESS";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }
}
