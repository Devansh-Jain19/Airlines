/**
 * AeroSkyline - Web Frontend Application Logic
 * Integrates directly with Spring Boot REST Backend on http://localhost:8081
 */

const API_BASE = 'http://localhost:8081/api';

// Application State
const state = {
    airports: [],
    searchResults: [],
    selectedFlight: null,
    selectedSeat: null,
    seatsMap: [],
    baggageCost: 0,
    currentUser: null,
    lastBooking: null
};

// DOM Elements
const el = {
    statusBadge: document.getElementById('backend-status-badge'),
    statusText: document.getElementById('backend-status-text'),
    statusDot: document.getElementById('status-dot'),
    originSelect: document.getElementById('origin-select'),
    destSelect: document.getElementById('destination-select'),
    departureDate: document.getElementById('departure-date'),
    swapAirportsBtn: document.getElementById('swap-airports-btn'),
    flightSearchForm: document.getElementById('flight-search-form'),
    searchSubmitBtn: document.getElementById('search-submit-btn'),
    resultsSection: document.getElementById('results-section'),
    resultsMetaText: document.getElementById('results-meta-text'),
    flightsList: document.getElementById('flights-list'),
    flightsLoading: document.getElementById('flights-loading'),
    flightsEmpty: document.getElementById('flights-empty'),
    sortSelect: document.getElementById('sort-select'),
    seatSection: document.getElementById('seat-section'),
    seatFlightSummary: document.getElementById('seat-flight-summary'),
    cabinRowsContainer: document.getElementById('cabin-rows-container'),
    backToFlightsBtn: document.getElementById('back-to-flights-btn'),
    summaryFlightNum: document.getElementById('summary-flight-num'),
    summaryRoute: document.getElementById('summary-route'),
    summaryDepartureTime: document.getElementById('summary-departure-time'),
    summarySeatNum: document.getElementById('summary-seat-num'),
    summarySeatClass: document.getElementById('summary-seat-class'),
    priceSeatVal: document.getElementById('price-seat-val'),
    priceBaggageVal: document.getElementById('price-baggage-val'),
    priceTaxesVal: document.getElementById('price-taxes-val'),
    priceTotalVal: document.getElementById('price-total-val'),
    proceedToCheckoutBtn: document.getElementById('proceed-to-checkout-btn'),
    checkoutModal: document.getElementById('checkout-modal'),
    closeCheckoutModal: document.getElementById('close-checkout-modal'),
    cancelCheckoutBtn: document.getElementById('cancel-checkout-btn'),
    checkoutForm: document.getElementById('checkout-form'),
    modalTotalDisplay: document.getElementById('modal-total-display'),
    boardingPassModal: document.getElementById('boarding-pass-modal'),
    closeTicketModal: document.getElementById('close-ticket-modal'),
    bookAnotherFlightBtn: document.getElementById('book-another-flight-btn'),
    viewMyBookingsNowBtn: document.getElementById('view-my-bookings-now-btn'),
    authModal: document.getElementById('auth-modal'),
    closeAuthModal: document.getElementById('close-auth-modal'),
    navLoginBtn: document.getElementById('nav-login-btn'),
    navBookingsBtn: document.getElementById('nav-bookings-btn'),
    authNavSlot: document.getElementById('auth-nav-slot'),
    loginForm: document.getElementById('login-form'),
    registerForm: document.getElementById('register-form'),
    tabLogin: document.getElementById('tab-login'),
    tabRegister: document.getElementById('tab-register'),
    bookingsModal: document.getElementById('bookings-modal'),
    closeBookingsModal: document.getElementById('close-bookings-modal'),
    bookingsListContent: document.getElementById('bookings-list-content')
};

// ==========================================================================
// INITIALIZATION
// ==========================================================================
document.addEventListener('DOMContentLoaded', () => {
    initDefaultDates();
    checkBackendHealthAndLoadAirports();
    setupEventListeners();
    loadSession();
});

function initDefaultDates() {
    const today = new Date();
    // Default search to tomorrow or 2026-10-08
    const defaultDate = new Date(today);
    defaultDate.setDate(defaultDate.getDate() + 1);
    
    const yyyy = defaultDate.getFullYear();
    const mm = String(defaultDate.getMonth() + 1).padStart(2, '0');
    const dd = String(defaultDate.getDate()).padStart(2, '0');
    el.departureDate.value = `${yyyy}-${mm}-${dd}`;
    el.departureDate.min = today.toISOString().split('T')[0];
}

// ==========================================================================
// BACKEND CONNECTIVITY & AIRPORTS
// ==========================================================================
async function checkBackendHealthAndLoadAirports() {
    try {
        const response = await fetch(`${API_BASE}/airports`);
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        
        state.airports = await response.json();
        
        // Update status badge
        el.statusText.textContent = 'Backend Connected (8081)';
        el.statusBadge.style.borderColor = 'rgba(16, 185, 129, 0.4)';
        el.statusDot.style.backgroundColor = '#10b981';

        populateAirportsDropdown();
    } catch (err) {
        console.error('Failed to connect to Spring Boot backend:', err);
        el.statusText.textContent = 'Backend Offline (8081)';
        el.statusBadge.style.borderColor = 'rgba(239, 68, 68, 0.4)';
        el.statusBadge.style.color = '#f87171';
        el.statusDot.style.backgroundColor = '#ef4444';
        el.statusDot.classList.remove('pulsing');
        showToast('Could not reach backend on http://localhost:8081', 'error');
    }
}

function populateAirportsDropdown() {
    el.originSelect.innerHTML = '<option value="" disabled selected>Select Origin Airport</option>';
    el.destSelect.innerHTML = '<option value="" disabled selected>Select Destination Airport</option>';

    state.airports.forEach(airport => {
        const code = airport.iataCode;
        const name = `${code} - ${airport.city} (${airport.name})`;

        const opt1 = new Option(name, code);
        const opt2 = new Option(name, code);
        el.originSelect.add(opt1);
        el.destSelect.add(opt2);
    });

    // Defaults: DEL -> BOM
    el.originSelect.value = 'DEL';
    el.destSelect.value = 'BOM';
}

// ==========================================================================
// EVENT LISTENERS
// ==========================================================================
function setupEventListeners() {
    // Swap Airports
    el.swapAirportsBtn.addEventListener('click', () => {
        const temp = el.originSelect.value;
        el.originSelect.value = el.destSelect.value;
        el.destSelect.value = temp;
    });

    // Quick Route Chips
    document.querySelectorAll('.chip-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            el.originSelect.value = btn.dataset.from;
            el.destSelect.value = btn.dataset.to;
            executeSearch();
        });
    });

    // Search Form Submit
    el.flightSearchForm.addEventListener('submit', (e) => {
        e.preventDefault();
        executeSearch();
    });

    // Sort Dropdown
    el.sortSelect.addEventListener('change', () => {
        sortAndRenderFlights();
    });

    // Back to Flights Button
    el.backToFlightsBtn.addEventListener('click', () => {
        el.seatSection.classList.add('hidden');
        el.resultsSection.classList.remove('hidden');
        window.scrollTo({ top: el.resultsSection.offsetTop - 80, behavior: 'smooth' });
    });

    // Baggage Radio Change
    document.querySelectorAll('input[name="baggage"]').forEach(radio => {
        radio.addEventListener('change', (e) => {
            document.querySelectorAll('.baggage-radio').forEach(r => r.classList.remove('selected'));
            e.target.closest('.baggage-radio').classList.add('selected');
            
            const weight = parseInt(e.target.value, 10);
            state.baggageCost = weight === 20 ? 750 : weight === 25 ? 1400 : 0;
            updatePriceCalculations();
        });
    });

    // Proceed to Checkout
    el.proceedToCheckoutBtn.addEventListener('click', () => {
        if (!state.selectedSeat) return;
        openCheckoutModal();
    });

    // Modal Closures
    el.closeCheckoutModal.addEventListener('click', () => el.checkoutModal.classList.add('hidden'));
    el.cancelCheckoutBtn.addEventListener('click', () => el.checkoutModal.classList.add('hidden'));
    el.closeTicketModal.addEventListener('click', () => el.boardingPassModal.classList.add('hidden'));
    el.closeAuthModal.addEventListener('click', () => el.authModal.classList.add('hidden'));
    el.closeBookingsModal.addEventListener('click', () => el.bookingsModal.classList.add('hidden'));

    // Checkout Form Submit
    el.checkoutForm.addEventListener('submit', handleCheckoutSubmit);

    // Boarding Pass Actions
    el.bookAnotherFlightBtn.addEventListener('click', () => {
        el.boardingPassModal.classList.add('hidden');
        el.seatSection.classList.add('hidden');
        el.resultsSection.classList.remove('hidden');
        window.scrollTo({ top: 0, behavior: 'smooth' });
    });

    el.viewMyBookingsNowBtn.addEventListener('click', () => {
        el.boardingPassModal.classList.add('hidden');
        openMyBookingsModal();
    });

    // Nav Bookings
    el.navBookingsBtn.addEventListener('click', openMyBookingsModal);

    // Auth Modal Tabs
    el.tabLogin.addEventListener('click', () => {
        el.tabLogin.classList.add('active');
        el.tabRegister.classList.remove('active');
        el.loginForm.classList.remove('hidden');
        el.registerForm.classList.add('hidden');
    });

    el.tabRegister.addEventListener('click', () => {
        el.tabRegister.classList.add('active');
        el.tabLogin.classList.remove('active');
        el.registerForm.classList.remove('hidden');
        el.loginForm.classList.add('hidden');
    });

    // Auth Forms Submit
    el.loginForm.addEventListener('submit', handleLogin);
    el.registerForm.addEventListener('submit', handleRegister);
    el.navLoginBtn.addEventListener('click', () => el.authModal.classList.remove('hidden'));

    // Payment method selector
    document.querySelectorAll('.pay-option').forEach(option => {
        option.addEventListener('click', () => {
            document.querySelectorAll('.pay-option').forEach(o => o.classList.remove('selected'));
            option.classList.add('selected');
        });
    });
}

// ==========================================================================
// FLIGHT SEARCH & DISPLAY
// ==========================================================================
async function executeSearch() {
    const from = el.originSelect.value;
    const to = el.destSelect.value;
    const date = el.departureDate.value;

    if (!from || !to) {
        showToast('Please select both departure and destination airports.', 'error');
        return;
    }
    if (from === to) {
        showToast('Origin and Destination cannot be identical.', 'error');
        return;
    }

    el.resultsSection.classList.remove('hidden');
    el.seatSection.classList.add('hidden');
    el.flightsList.innerHTML = '';
    el.flightsLoading.classList.remove('hidden');
    el.flightsEmpty.classList.add('hidden');
    el.resultsMetaText.textContent = `Searching for flights from ${from} to ${to} on ${date}...`;

    try {
        const url = `${API_BASE}/flights/search?from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}&date=${encodeURIComponent(date)}`;
        const res = await fetch(url);
        if (!res.ok) throw new Error(`Server returned ${res.status}`);

        state.searchResults = await res.json();
        el.flightsLoading.classList.add('hidden');

        if (!state.searchResults || state.searchResults.length === 0) {
            el.flightsEmpty.classList.remove('hidden');
            el.resultsMetaText.textContent = `No scheduled flights found for ${from} ➔ ${to} on ${date}.`;
        } else {
            el.resultsMetaText.textContent = `Found ${state.searchResults.length} available flight(s) from ${from} to ${to}.`;
            sortAndRenderFlights();
        }

        window.scrollTo({ top: el.resultsSection.offsetTop - 80, behavior: 'smooth' });
    } catch (err) {
        console.error('Search error:', err);
        el.flightsLoading.classList.add('hidden');
        showToast(`Flight search failed: ${err.message}`, 'error');
    }
}

function sortAndRenderFlights() {
    const sortVal = el.sortSelect.value;
    const flights = [...state.searchResults];

    if (sortVal === 'price-asc') {
        flights.sort((a, b) => a.basePrice - b.basePrice);
    } else if (sortVal === 'price-desc') {
        flights.sort((a, b) => b.basePrice - a.basePrice);
    } else if (sortVal === 'duration-asc') {
        flights.sort((a, b) => (a.durationMinutes || 0) - (b.durationMinutes || 0));
    }

    renderFlightCards(flights);
}

function renderFlightCards(flights) {
    el.flightsList.innerHTML = '';

    flights.forEach(f => {
        const depTime = formatTime(f.departureTime);
        const arrTime = formatTime(f.arrivalTime);
        const priceFmt = formatCurrency(f.basePrice);
        const card = document.createElement('div');
        card.className = 'flight-card';

        card.innerHTML = `
            <div class="airline-badge">
                <div class="airline-logo-circle">✈</div>
                <div>
                    <div class="airline-name">${f.airlineName || 'Airline'}</div>
                    <div class="flight-code">${f.flightNumber}</div>
                </div>
            </div>

            <div class="schedule-route">
                <div class="station-time">
                    <span class="time-val">${depTime}</span>
                    <span class="station-iata">${f.departureAirportIata || f.departureAirportCode}</span>
                    <span class="station-city">${f.departureCity || ''}</span>
                </div>

                <div class="flight-duration-track">
                    <span class="duration-text">${f.duration || '2h 15m'}</span>
                    <div class="track-line">
                        <span class="track-plane">✈</span>
                    </div>
                    <span class="stop-type">NON-STOP</span>
                </div>

                <div class="station-time right">
                    <span class="time-val">${arrTime}</span>
                    <span class="station-iata">${f.arrivalAirportIata || f.arrivalAirportCode}</span>
                    <span class="station-city">${f.arrivalCity || ''}</span>
                </div>
            </div>

            <div class="pricing-action">
                <span class="seats-left-pill">${f.availableSeats || f.seatsAvailable || 48} seats available</span>
                <span class="price-display">${priceFmt}</span>
                <button class="btn-select-flight" data-flight-id="${f.id || f.flightId}">
                    Select Seats →
                </button>
            </div>
        `;

        card.querySelector('.btn-select-flight').addEventListener('click', () => {
            openSeatSelection(f);
        });

        el.flightsList.appendChild(card);
    });
}

// ==========================================================================
// SEAT SELECTION & CABIN BLUEPRINT
// ==========================================================================
async function openSeatSelection(flight) {
    state.selectedFlight = flight;
    state.selectedSeat = null;

    el.resultsSection.classList.add('hidden');
    el.seatSection.classList.remove('hidden');

    el.seatFlightSummary.textContent = `${flight.airlineName} ${flight.flightNumber} • ${flight.departureAirportIata} ➔ ${flight.arrivalAirportIata}`;
    el.summaryFlightNum.textContent = flight.flightNumber;
    el.summaryRoute.textContent = `${flight.departureAirportIata} ➔ ${flight.arrivalAirportIata}`;
    el.summaryDepartureTime.textContent = `${formatDate(flight.departureTime)} at ${formatTime(flight.departureTime)}`;
    el.summarySeatNum.textContent = 'None Selected';
    el.summarySeatClass.textContent = '-';
    el.proceedToCheckoutBtn.disabled = true;

    updatePriceCalculations();

    // Fetch Seats from backend
    try {
        const flightId = flight.id || flight.flightId;
        const res = await fetch(`${API_BASE}/flights/${flightId}/seats`);
        if (!res.ok) throw new Error(`Could not load seats (status ${res.status})`);

        state.seatsMap = await res.json();
        renderCabinSeats(state.seatsMap);
    } catch (err) {
        console.error('Error fetching seats:', err);
        showToast('Could not load aircraft seat layout', 'error');
    }

    window.scrollTo({ top: el.seatSection.offsetTop - 80, behavior: 'smooth' });
}

function renderCabinSeats(seats) {
    el.cabinRowsContainer.innerHTML = '';

    // Group seats by row number (e.g. 1A -> row 1)
    const rows = {};
    seats.forEach(s => {
        const match = s.seatNumber.match(/^(\d+)([A-Z])$/);
        if (match) {
            const rowNum = parseInt(match[1], 10);
            const colLetter = match[2];
            if (!rows[rowNum]) rows[rowNum] = {};
            rows[rowNum][colLetter] = s;
        }
    });

    Object.keys(rows).sort((a, b) => a - b).forEach(rowNum => {
        const rowData = rows[rowNum];
        const rowEl = document.createElement('div');
        rowEl.className = 'seat-row';

        const isBusiness = rowNum <= 2;
        const seatClass = isBusiness ? 'business' : 'economy';

        // Left Group (A, B)
        const leftGroup = document.createElement('div');
        leftGroup.className = 'seat-group';
        ['A', 'B'].forEach(col => {
            const seat = rowData[col];
            if (seat) {
                leftGroup.appendChild(createSeatButton(seat, seatClass));
            }
        });

        // Row label in aisle
        const aisle = document.createElement('div');
        aisle.className = 'aisle-gap';
        aisle.textContent = rowNum;

        // Right Group (C, D)
        const rightGroup = document.createElement('div');
        rightGroup.className = 'seat-group';
        ['C', 'D'].forEach(col => {
            const seat = rowData[col];
            if (seat) {
                rightGroup.appendChild(createSeatButton(seat, seatClass));
            }
        });

        rowEl.appendChild(leftGroup);
        rowEl.appendChild(aisle);
        rowEl.appendChild(rightGroup);
        el.cabinRowsContainer.appendChild(rowEl);
    });
}

function createSeatButton(seat, seatClass) {
    const btn = document.createElement('button');
    btn.type = 'button';
    btn.className = `seat-btn ${seatClass}`;
    btn.textContent = seat.seatNumber;
    btn.title = `Seat ${seat.seatNumber} (${seat.seatClass}) - ${formatCurrency(seat.calculatedPrice || seat.price)}`;

    if (!seat.available) {
        btn.classList.add('occupied');
        btn.disabled = true;
    } else {
        btn.addEventListener('click', () => {
            selectSeat(seat, btn);
        });
    }

    return btn;
}

function selectSeat(seat, buttonEl) {
    document.querySelectorAll('.seat-btn').forEach(b => b.classList.remove('selected'));
    buttonEl.classList.add('selected');

    state.selectedSeat = seat;
    el.summarySeatNum.textContent = seat.seatNumber;
    el.summarySeatClass.textContent = seat.seatClass;
    el.proceedToCheckoutBtn.disabled = false;

    updatePriceCalculations();
}

function updatePriceCalculations() {
    const seatPrice = state.selectedSeat ? (state.selectedSeat.calculatedPrice || state.selectedSeat.price || state.selectedFlight.basePrice) : 0;
    const baggage = state.baggageCost;
    const subtotal = seatPrice + baggage;
    const taxes = Math.round(subtotal * 0.12);
    const total = subtotal + taxes;

    el.priceSeatVal.textContent = formatCurrency(seatPrice);
    el.priceBaggageVal.textContent = baggage > 0 ? formatCurrency(baggage) : 'Included';
    el.priceTaxesVal.textContent = formatCurrency(taxes);
    el.priceTotalVal.textContent = formatCurrency(total);
    el.modalTotalDisplay.textContent = formatCurrency(total);

    state.currentTotalAmount = total;
}

// ==========================================================================
// CHECKOUT & BOOKING CREATION
// ==========================================================================
function openCheckoutModal() {
    el.checkoutModal.classList.remove('hidden');

    // Auto-fill passenger details if user is logged in
    if (state.currentUser) {
        document.getElementById('cust-first-name').value = state.currentUser.firstName || '';
        document.getElementById('cust-last-name').value = state.currentUser.lastName || '';
        document.getElementById('cust-email').value = state.currentUser.email || '';
        document.getElementById('cust-phone').value = state.currentUser.phone || '+91 9876543210';
        document.getElementById('cust-passport').value = state.currentUser.passportNumber || 'K8291034';
    } else {
        // Defaults to demo user Aarav Sharma
        document.getElementById('cust-first-name').value = 'Aarav';
        document.getElementById('cust-last-name').value = 'Sharma';
        document.getElementById('cust-email').value = 'aarav@gmail.com';
        document.getElementById('cust-phone').value = '+91 9876543210';
        document.getElementById('cust-passport').value = 'P9823102';
    }
}

async function handleCheckoutSubmit(e) {
    e.preventDefault();

    const submitBtn = document.getElementById('submit-booking-btn');
    const payText = document.getElementById('btn-pay-text');
    const paySpinner = document.getElementById('pay-spinner');

    submitBtn.disabled = true;
    payText.textContent = 'Processing Payment & Issuing Ticket...';
    paySpinner.classList.remove('hidden');

    const firstName = document.getElementById('cust-first-name').value;
    const lastName = document.getElementById('cust-last-name').value;
    const email = document.getElementById('cust-email').value;
    const phone = document.getElementById('cust-phone').value;
    const passport = document.getElementById('cust-passport').value;
    const paymentMethod = document.querySelector('input[name="payment-method"]:checked').value;

    const passengerId = state.currentUser ? (state.currentUser.passengerId || state.currentUser.id || 1) : 1;
    const flightId = state.selectedFlight.id || state.selectedFlight.flightId;
    const seatId = state.selectedSeat.id || state.selectedSeat.seatId;
    const seatNum = state.selectedSeat.seatNumber;

    try {
        // 1. Create Booking in Spring Boot
        const bookingPayload = {
            passengerId: passengerId,
            flightId: flightId,
            seatIds: [seatId],
            seatNumber: seatNum,
            totalFare: state.currentTotalAmount
        };

        const bookingRes = await fetch(`${API_BASE}/bookings`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(bookingPayload)
        });

        if (!bookingRes.ok) {
            const errText = await bookingRes.text();
            throw new Error(errText || 'Booking creation failed');
        }

        const booking = await bookingRes.json();
        const bookingId = booking.id || booking.bookingId;

        // 2. Process Payment in Spring Boot
        const paymentPayload = {
            bookingId: bookingId,
            amount: state.currentTotalAmount,
            paymentMethod: paymentMethod,
            method: paymentMethod,
            transactionReference: 'TXN-' + Math.random().toString(36).substring(2, 9).toUpperCase()
        };

        const payRes = await fetch(`${API_BASE}/payments`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(paymentPayload)
        });

        if (!payRes.ok) {
            console.warn('Payment API returned non-200, but booking exists:', await payRes.text());
        }

        state.lastBooking = {
            ...booking,
            passengerName: `${firstName} ${lastName}`,
            email: email,
            phone: phone,
            passport: passport,
            seatNumber: seatNum,
            flight: state.selectedFlight,
            fare: state.currentTotalAmount
        };

        showToast('Payment successful! Boarding pass issued.', 'success');
        el.checkoutModal.classList.add('hidden');
        renderBoardingPass(state.lastBooking);
    } catch (err) {
        console.error('Booking submission failed:', err);
        showToast(`Failed: ${err.message}`, 'error');
    } finally {
        submitBtn.disabled = false;
        payText.textContent = 'Pay & Generate Boarding Pass';
        paySpinner.classList.add('hidden');
    }
}

// ==========================================================================
// BOARDING PASS RENDERING
// ==========================================================================
function renderBoardingPass(booking) {
    const flight = booking.flight;
    const pnr = booking.bookingReference || ('AMS' + Math.random().toString(36).substring(2, 6).toUpperCase());

    document.getElementById('pass-airline-name').textContent = `${flight.airlineName} Airlines`;
    document.getElementById('pass-cabin-class').textContent = `${state.selectedSeat.seatClass} CLASS`;
    document.getElementById('pass-origin-code').textContent = flight.departureAirportIata || flight.departureAirportCode;
    document.getElementById('pass-origin-city').textContent = flight.departureCity || 'Origin';
    document.getElementById('pass-dest-code').textContent = flight.arrivalAirportIata || flight.arrivalAirportCode;
    document.getElementById('pass-dest-city').textContent = flight.arrivalCity || 'Destination';
    document.getElementById('pass-duration').textContent = flight.duration || '2h 15m';

    document.getElementById('pass-passenger-name').textContent = booking.passengerName || 'Passenger';
    document.getElementById('pass-flight-number').textContent = flight.flightNumber;
    document.getElementById('pass-departure-time').textContent = formatTime(flight.departureTime);
    document.getElementById('pass-flight-date').textContent = formatDate(flight.departureTime);
    document.getElementById('pass-seat-number').textContent = booking.seatNumber;
    document.getElementById('pass-pnr-code').textContent = pnr;
    document.getElementById('pass-ticket-id').textContent = `ETKT-${Math.floor(10000000 + Math.random() * 90000000)}`;

    el.boardingPassModal.classList.remove('hidden');
}

// ==========================================================================
// AUTHENTICATION & SESSION
// ==========================================================================
function loadSession() {
    const saved = localStorage.getItem('aeroskyline_user');
    if (saved) {
        try {
            state.currentUser = JSON.parse(saved);
            updateNavForUser(state.currentUser);
        } catch (e) {
            localStorage.removeItem('aeroskyline_user');
        }
    }
}

function updateNavForUser(user) {
    if (user) {
        el.authNavSlot.innerHTML = `
            <div style="display:flex;align-items:center;gap:0.75rem;">
                <span style="font-weight:700;font-size:0.9rem;color:#38bdf8;">👤 ${user.name || user.firstName || 'Traveler'}</span>
                <button class="nav-btn" id="logout-btn" style="padding:0.3rem 0.6rem;font-size:0.78rem;">Sign Out</button>
            </div>
        `;
        document.getElementById('logout-btn').addEventListener('click', handleLogout);
    } else {
        el.authNavSlot.innerHTML = `
            <button class="btn-primary-sm" id="nav-login-btn">
                <svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                Sign In
            </button>
        `;
        document.getElementById('nav-login-btn').addEventListener('click', () => el.authModal.classList.remove('hidden'));
    }
}

async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;

    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        if (!res.ok) throw new Error('Invalid email or password');

        const authData = await res.json();
        state.currentUser = authData.passenger || {
            passengerId: authData.passengerId,
            email: authData.email,
            name: authData.name,
            firstName: authData.name ? authData.name.split(' ')[0] : 'User'
        };

        localStorage.setItem('aeroskyline_user', JSON.stringify(state.currentUser));
        updateNavForUser(state.currentUser);
        el.authModal.classList.add('hidden');
        showToast(`Welcome back, ${state.currentUser.firstName || 'Traveler'}!`, 'success');
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function handleRegister(e) {
    e.preventDefault();
    const req = {
        firstName: document.getElementById('reg-first-name').value,
        lastName: document.getElementById('reg-last-name').value,
        email: document.getElementById('reg-email').value,
        password: document.getElementById('reg-password').value,
        phone: document.getElementById('reg-phone').value,
        passportNumber: document.getElementById('reg-passport').value
    };

    try {
        const res = await fetch(`${API_BASE}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(req)
        });

        if (!res.ok) throw new Error('Registration failed. Email or phone may already exist.');

        const registered = await res.json();
        state.currentUser = registered;
        localStorage.setItem('aeroskyline_user', JSON.stringify(state.currentUser));
        updateNavForUser(state.currentUser);
        el.authModal.classList.add('hidden');
        showToast('Account created successfully!', 'success');
    } catch (err) {
        showToast(err.message, 'error');
    }
}

function handleLogout() {
    state.currentUser = null;
    localStorage.removeItem('aeroskyline_user');
    updateNavForUser(null);
    showToast('Signed out successfully', 'info');
}

// ==========================================================================
// MY BOOKINGS DRAWER
// ==========================================================================
async function openMyBookingsModal() {
    el.bookingsModal.classList.remove('hidden');
    el.bookingsListContent.innerHTML = `
        <div class="loading-state">
            <div class="spinner"></div>
            <p>Fetching your reservations from database...</p>
        </div>
    `;

    const passengerId = state.currentUser ? (state.currentUser.passengerId || state.currentUser.id || 1) : 1;

    try {
        const res = await fetch(`${API_BASE}/passengers/${passengerId}/bookings`);
        if (!res.ok) throw new Error('Failed to retrieve bookings');

        const bookings = await res.json();
        renderBookingsList(bookings);
    } catch (err) {
        console.error('Fetch bookings error:', err);
        el.bookingsListContent.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">📂</div>
                <h3>No Bookings Found</h3>
                <p>You have not made any flight bookings yet or the server could not be reached.</p>
            </div>
        `;
    }
}

function renderBookingsList(bookings) {
    if (!bookings || bookings.length === 0) {
        el.bookingsListContent.innerHTML = `
            <div class="empty-state">
                <div class="empty-icon">✈️</div>
                <h3>No Bookings Yet</h3>
                <p>Ready to travel? Search flights above to make your first reservation.</p>
            </div>
        `;
        return;
    }

    el.bookingsListContent.innerHTML = '<div style="display:flex;flex-direction:column;gap:1rem;"></div>';
    const listWrapper = el.bookingsListContent.firstElementChild;

    bookings.forEach(b => {
        const card = document.createElement('div');
        card.className = 'glass-panel';
        card.style.padding = '1.25rem 1.5rem';
        card.style.display = 'flex';
        card.style.justifyContent = 'space-between';
        card.style.alignItems = 'center';
        card.style.flexWrap = 'wrap';
        card.style.gap = '1rem';

        const isCancelled = b.status === 'CANCELLED';
        const statusBadge = isCancelled 
            ? '<span style="color:#f87171;background:rgba(239,68,68,0.15);padding:0.2rem 0.6rem;border-radius:999px;font-size:0.75rem;font-weight:700;">CANCELLED</span>'
            : '<span style="color:#34d399;background:rgba(16,185,129,0.15);padding:0.2rem 0.6rem;border-radius:999px;font-size:0.75rem;font-weight:700;">CONFIRMED</span>';

        card.innerHTML = `
            <div>
                <div style="display:flex;align-items:center;gap:0.75rem;margin-bottom:0.35rem;">
                    <span style="font-family:var(--font-mono);font-size:1.1rem;font-weight:800;color:#38bdf8;">${b.bookingReference || ('AMS' + b.id)}</span>
                    ${statusBadge}
                </div>
                <div style="font-size:0.9rem;font-weight:600;color:#f8fafc;">
                    Flight ${b.flightNumber || 'Scheduled Flight'} • Seat: ${b.seatNumber || 'Assigned'}
                </div>
                <div style="font-size:0.78rem;color:var(--text-muted);margin-top:0.2rem;">
                    Booked on ${formatDate(b.bookingDate || new Date())} • Total: ${formatCurrency(b.totalFare || b.totalAmount || 5240)}
                </div>
            </div>

            <div>
                ${!isCancelled ? `<button class="btn-outline" style="border-color:#ef4444;color:#f87171;padding:0.4rem 0.9rem;font-size:0.82rem;" data-cancel-id="${b.id || b.bookingId}">Cancel Booking</button>` : ''}
            </div>
        `;

        const cancelBtn = card.querySelector('[data-cancel-id]');
        if (cancelBtn) {
            cancelBtn.addEventListener('click', async () => {
                if (confirm('Are you sure you want to cancel this booking and release the seat?')) {
                    await cancelBooking(b.id || b.bookingId);
                }
            });
        }

        listWrapper.appendChild(card);
    });
}

async function cancelBooking(bookingId) {
    try {
        const res = await fetch(`${API_BASE}/bookings/${bookingId}`, {
            method: 'DELETE'
        });

        if (!res.ok) throw new Error('Cancellation failed');

        showToast('Booking cancelled successfully', 'info');
        openMyBookingsModal(); // Refresh
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// ==========================================================================
// UTILITY FUNCTIONS
// ==========================================================================
function formatCurrency(amount) {
    const num = parseFloat(amount) || 0;
    return '₹' + num.toLocaleString('en-IN', { maximumFractionDigits: 0 });
}

function formatTime(isoStr) {
    if (!isoStr) return '--:--';
    try {
        const d = new Date(isoStr);
        return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: true });
    } catch (e) {
        return isoStr.substring(11, 16) || '--:--';
    }
}

function formatDate(isoStr) {
    if (!isoStr) return 'Scheduled';
    try {
        const d = new Date(isoStr);
        return d.toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' });
    } catch (e) {
        return isoStr.split('T')[0];
    }
}

function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    
    const icon = type === 'success' ? '✓' : type === 'error' ? '✕' : 'ℹ';
    toast.innerHTML = `<span>${icon}</span><span>${message}</span>`;

    container.appendChild(toast);
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(10px)';
        toast.style.transition = 'all 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}
