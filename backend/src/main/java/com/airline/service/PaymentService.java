package com.airline.service;

import com.airline.dto.request.PaymentRequest;
import com.airline.dto.response.PaymentResponseDto;
import com.airline.entity.*;
import com.airline.entity.enums.BookingStatus;
import com.airline.entity.enums.PaymentStatus;
import com.airline.entity.enums.TicketStatus;
import com.airline.exception.PaymentFailedException;
import com.airline.exception.ResourceNotFoundException;
import com.airline.mapper.EntityDtoMapper;
import com.airline.repository.BookingRepository;
import com.airline.repository.PaymentRepository;
import com.airline.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired private PaymentRepository paymentRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private TicketRepository ticketRepository;

    @Transactional(rollbackFor = Exception.class)
    public PaymentResponseDto processPayment(PaymentRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + request.getBookingId()));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new PaymentFailedException("Cannot process payment for a cancelled booking.");
        }

        BigDecimal paymentAmount = request.getAmount() != null ?
            BigDecimal.valueOf(request.getAmount()) : booking.getTotalAmount();

        String transactionRef = request.getTransactionReference() != null ?
            request.getTransactionReference() : "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setPaymentMethod(request.getPaymentMethod() != null ? request.getPaymentMethod() : "UPI");
        payment.setAmount(paymentAmount);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setTransactionRef(transactionRef);
        payment.setPaymentTime(LocalDateTime.now());
        payment = paymentRepository.save(payment);

        // Transition Booking & Tickets status to CONFIRMED
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        List<Ticket> tickets = ticketRepository.findByBookingBookingId(booking.getBookingId());
        for (Ticket ticket : tickets) {
            ticket.setStatus(TicketStatus.CONFIRMED);
            ticketRepository.save(ticket);
        }

        return EntityDtoMapper.toPaymentDto(payment);
    }
}
