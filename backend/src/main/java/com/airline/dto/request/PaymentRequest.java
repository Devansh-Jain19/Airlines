package com.airline.dto.request;

public class PaymentRequest {
    private Long bookingId;
    private Double amount;
    private String paymentMethod;
    private String method;
    private String transactionReference;
    private String transactionRef;

    public PaymentRequest() {}

    public Long getBookingId() { return bookingId; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getPaymentMethod() {
        return paymentMethod != null ? paymentMethod : method;
    }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getTransactionReference() {
        return transactionReference != null ? transactionReference : transactionRef;
    }
    public void setTransactionReference(String transactionReference) { this.transactionReference = transactionReference; }

    public String getTransactionRef() { return transactionRef; }
    public void setTransactionRef(String transactionRef) { this.transactionRef = transactionRef; }
}
