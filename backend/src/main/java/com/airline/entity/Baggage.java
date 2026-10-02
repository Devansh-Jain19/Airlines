package com.airline.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "baggage")
public class Baggage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "baggage_id")
    private Long baggageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Column(name = "weight_kg", nullable = false)
    private Double weightKg;

    @Column(name = "fee", precision = 10, scale = 2)
    private BigDecimal fee;

    public Baggage() {}

    public Long getBaggageId() { return baggageId; }
    public void setBaggageId(Long baggageId) { this.baggageId = baggageId; }

    public Ticket getTicket() { return ticket; }
    public void setTicket(Ticket ticket) { this.ticket = ticket; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public BigDecimal getFee() { return fee; }
    public void setFee(BigDecimal fee) { this.fee = fee; }
}
