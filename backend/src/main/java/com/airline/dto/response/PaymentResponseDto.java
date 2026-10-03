package com.airline.dto.response;

import java.math.BigDecimal;

public class PaymentResponseDto {
    private Long id;
    private Long paymentId;
    private Long bookingId;
    private String paymentMethod;
    private String method;
    private BigDecimal amount;
    private String status;
    private String transactionId;
    private String transactionRef;
    private String paymentTime;
    private String bookingStatus;

    public PaymentResponseDto() {}

    public Long getId() { return id != null ? id : paymentId; }
    public void setId(Long id) { this.id = id; this.paymentId = id; }

    public Long getPaymentId() { return paymentId != null ? paymentId : id; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; this.id = paymentId; }

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public String getPaymentMethod() { return paymentMethod != null ? paymentMethod : method; }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
        this.method = paymentMethod;
    }

    public String getMethod() { return method != null ? method : paymentMethod; }
    public void setMethod(String method) {
        this.method = method;
        this.paymentMethod = method;
    }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTransactionId() { return transactionId != null ? transactionId : transactionRef; }
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
        this.transactionRef = transactionId;
    }

    public String getTransactionRef() { return transactionRef != null ? transactionRef : transactionId; }
    public void setTransactionRef(String transactionRef) {
        this.transactionRef = transactionRef;
        this.transactionId = transactionRef;
    }

    public String getPaymentTime() { return paymentTime; }
    public void setPaymentTime(String paymentTime) { this.paymentTime = paymentTime; }

    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }
}
