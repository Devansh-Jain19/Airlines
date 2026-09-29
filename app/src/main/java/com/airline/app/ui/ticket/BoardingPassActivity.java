package com.airline.app.ui.ticket;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.airline.app.databinding.ActivityBoardingPassBinding;
import com.airline.app.model.response.BookingResponseDto;
import com.airline.app.model.response.FlightSummaryDto;
import com.airline.app.model.response.TicketDetailDto;
import com.airline.app.network.Resource;
import com.airline.app.ui.search.MainActivity;
import com.airline.app.util.DateUtils;
import com.airline.app.util.SessionManager;
import com.airline.app.viewmodel.BookingViewModel;

public class BoardingPassActivity extends AppCompatActivity {
    public static final String EXTRA_BOOKING = "extra_booking";
    public static final String EXTRA_FLIGHT_SUMMARY = "extra_flight_summary";
    public static final String EXTRA_SEAT_NUMBER = "extra_seat_number";
    public static final String EXTRA_SEAT_CLASS = "extra_seat_class";
    public static final String EXTRA_TICKET_ID = "extra_ticket_id";

    private ActivityBoardingPassBinding binding;
    private BookingViewModel bookingViewModel;
    private SessionManager sessionManager;

    private BookingResponseDto booking;
    private FlightSummaryDto flightSummary;
    private String seatNumber;
    private String seatClass;
    private Long ticketId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);

        binding = ActivityBoardingPassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        booking = (BookingResponseDto) getIntent().getSerializableExtra(EXTRA_BOOKING);
        flightSummary = (FlightSummaryDto) getIntent().getSerializableExtra(EXTRA_FLIGHT_SUMMARY);
        seatNumber = getIntent().getStringExtra(EXTRA_SEAT_NUMBER);
        seatClass = getIntent().getStringExtra(EXTRA_SEAT_CLASS);
        ticketId = getIntent().hasExtra(EXTRA_TICKET_ID) ? getIntent().getLongExtra(EXTRA_TICKET_ID, 0) : null;

        setupToolbar();
        populateInitialDetails();

        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        if (ticketId != null && ticketId > 0) {
            bookingViewModel.getTicketResult().observe(this, resource -> {
                if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    populateFromTicket(resource.data);
                }
            });
            bookingViewModel.loadTicket(ticketId);
        }

        binding.btnDone.setOnClickListener(v -> {
            Intent intent = new Intent(BoardingPassActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void populateInitialDetails() {
        String passengerName = sessionManager.getName();
        if (booking != null && booking.getPassengerName() != null) {
            passengerName = booking.getPassengerName();
        }
        binding.tvTicketPassenger.setText(passengerName);

        String ref = (booking != null && booking.getBookingReference() != null) ? booking.getBookingReference() : "BK-" + (int)(Math.random() * 90000 + 10000);
        binding.tvTicketRef.setText(ref);

        if (flightSummary != null) {
            binding.tvTicketAirline.setText(flightSummary.getAirlineName());
            binding.tvTicketFlightNum.setText(flightSummary.getFlightNumber());
            binding.tvOriginIata.setText(flightSummary.getDepartureAirportIata());
            binding.tvOriginCity.setText(flightSummary.getDepartureCity());
            binding.tvDestIata.setText(flightSummary.getArrivalAirportIata());
            binding.tvDestCity.setText(flightSummary.getArrivalCity());
            binding.tvTicketDate.setText(DateUtils.formatIsoToDisplayTime(flightSummary.getDepartureTime()));
        }

        String finalSeat = seatNumber != null ? seatNumber : (booking != null ? booking.getSeatNumber() : "12A");
        binding.tvTicketSeat.setText(finalSeat);
        binding.tvTicketGate.setText("G12");
        binding.tvTicketTerminal.setText("T2");
        binding.tvBarcodeNumber.setText("AIR-" + ref.replace("-", "") + "-" + finalSeat);
    }

    private void populateFromTicket(TicketDetailDto ticket) {
        if (ticket == null) return;

        if (ticket.getPassengerName() != null) binding.tvTicketPassenger.setText(ticket.getPassengerName());
        if (ticket.getBookingReference() != null) binding.tvTicketRef.setText(ticket.getBookingReference());
        if (ticket.getAirlineName() != null) binding.tvTicketAirline.setText(ticket.getAirlineName());
        if (ticket.getFlightNumber() != null) binding.tvTicketFlightNum.setText(ticket.getFlightNumber());
        if (ticket.getOriginAirport() != null) binding.tvOriginIata.setText(ticket.getOriginAirport());
        if (ticket.getOriginCity() != null) binding.tvOriginCity.setText(ticket.getOriginCity());
        if (ticket.getDestinationAirport() != null) binding.tvDestIata.setText(ticket.getDestinationAirport());
        if (ticket.getDestinationCity() != null) binding.tvDestCity.setText(ticket.getDestinationCity());
        if (ticket.getDepartureTime() != null) binding.tvTicketDate.setText(DateUtils.formatIsoToDisplayTime(ticket.getDepartureTime()));
        if (ticket.getSeatNumber() != null) binding.tvTicketSeat.setText(ticket.getSeatNumber());
        if (ticket.getGate() != null) binding.tvTicketGate.setText(ticket.getGate());
        if (ticket.getTerminal() != null) binding.tvTicketTerminal.setText(ticket.getTerminal());
        if (ticket.getBarcode() != null) binding.tvBarcodeNumber.setText(ticket.getBarcode());
    }
}
