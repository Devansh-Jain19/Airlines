/**
 * AeroSkyline - Web Frontend Application Logic
 * Integrates directly with Spring Boot REST Backend on http://localhost:8081
 */

const API_BASE = 'http://localhost:8081/api';

// Application State
const state = {
    airports: [],
    searchResults: [],
    returnSearchResults: [],
    tripType: 'one-way', // 'one-way' | 'round-trip'
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
    returnDate: document.getElementById('return-date'),
    returnDateGroup: document.getElementById('return-date-group'),
    formGrid: document.getElementById('form-grid'),
    originHint: document.getElementById('origin-city-hint'),
    destHint: document.getElementById('dest-city-hint'),
    tripTabs: document.querySelectorAll('.search-tabs .tab-btn'),
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
    bookingsListContent: document.getElementById('bookings-list-content'),
    navConfirmTicketBtn: document.getElementById('nav-confirm-ticket-btn'),
    confirmTicketSection: document.getElementById('confirm-ticket-section'),
    confirmBackToSearchBtn: document.getElementById('confirm-back-to-search-btn'),
    copyPnrBtn: document.getElementById('copy-pnr-btn'),
    pagePrintTicketBtn: document.getElementById('page-print-ticket-btn'),
    pageBookAnotherBtn: document.getElementById('page-book-another-btn'),
    pageViewAllBookingsBtn: document.getElementById('page-view-all-bookings-btn'),
    pnrLookupForm: document.getElementById('pnr-lookup-form'),
    lookupPnrInput: document.getElementById('lookup-pnr-input')
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
    if (el.departureDate) {
        el.departureDate.value = `${yyyy}-${mm}-${dd}`;
        el.departureDate.min = today.toISOString().split('T')[0];
    }

    // Default return date is 3 days after departure
    const returnDateObj = new Date(defaultDate);
    returnDateObj.setDate(returnDateObj.getDate() + 3);
    const rYyyy = returnDateObj.getFullYear();
    const rMm = String(returnDateObj.getMonth() + 1).padStart(2, '0');
    const rDd = String(returnDateObj.getDate()).padStart(2, '0');
    if (el.returnDate) {
        el.returnDate.value = `${rYyyy}-${rMm}-${rDd}`;
        el.returnDate.min = `${yyyy}-${mm}-${dd}`;
    }
}

// ==========================================================================
// BACKEND CONNECTIVITY & AIRPORTS
// ==========================================================================
async function checkBackendHealthAndLoadAirports() {
    try {
        const response = await fetch(`${API_BASE}/airports`);
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        
        state.airports = await response.json();
        
        // Update status badge if element exists
        if (el.statusText) el.statusText.textContent = 'Backend Connected (8081)';
        if (el.statusBadge) el.statusBadge.style.borderColor = 'rgba(39, 168, 68, 0.4)';
        if (el.statusDot) el.statusDot.style.backgroundColor = '#27A844';

        populateAirportsDropdown();
    } catch (err) {
        console.error('Failed to connect to Spring Boot backend:', err);
        if (el.statusText) el.statusText.textContent = 'Backend Offline (8081)';
        if (el.statusBadge) {
            el.statusBadge.style.borderColor = 'rgba(239, 68, 68, 0.4)';
            el.statusBadge.style.color = '#f87171';
        }
        if (el.statusDot) {
            el.statusDot.style.backgroundColor = '#ef4444';
            el.statusDot.classList.remove('pulsing');
        }
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

    updateStationConnectivityHints();
}

function updateStationConnectivityHints() {
    const from = el.originSelect ? el.originSelect.value : '';
    const to = el.destSelect ? el.destSelect.value : '';
    const totalStations = (state.airports && state.airports.length > 0) ? state.airports.length : 25;

    if (el.originHint && from) {
        el.originHint.textContent = `${totalStations - 1} connected destination stations available for booking tickets (one-way & return)`;
    }
    if (el.destHint && to) {
        el.destHint.textContent = `${totalStations - 1} origin stations connected with reciprocal return flights`;
    }
}

// ==========================================================================
// EVENT LISTENERS
// ==========================================================================
function setupEventListeners() {
    // Trip Type Selector (One Way vs Round Trip)
    if (el.tripTabs) {
        el.tripTabs.forEach(tab => {
            tab.addEventListener('click', () => {
                const trip = tab.dataset.trip;
                if (!trip) return;
                state.tripType = trip;
                el.tripTabs.forEach(t => t.classList.remove('active'));
                tab.classList.add('active');

                if (trip === 'round-trip') {
                    if (el.returnDateGroup) el.returnDateGroup.classList.remove('hidden');
                    if (el.formGrid) el.formGrid.classList.add('round-trip-active');
                    if (el.searchSubmitBtn) {
                        const btnText = el.searchSubmitBtn.querySelector('.btn-text');
                        if (btnText) btnText.textContent = 'Search Flights (Round Trip)';
                    }
                } else {
                    if (el.returnDateGroup) el.returnDateGroup.classList.add('hidden');
                    if (el.formGrid) el.formGrid.classList.remove('round-trip-active');
                    if (el.searchSubmitBtn) {
                        const btnText = el.searchSubmitBtn.querySelector('.btn-text');
                        if (btnText) btnText.textContent = 'Search Flights';
                    }
                }
            });
        });
    }

    // Origin / Destination Select change listeners
    if (el.originSelect) {
        el.originSelect.addEventListener('change', updateStationConnectivityHints);
    }
    if (el.destSelect) {
        el.destSelect.addEventListener('change', updateStationConnectivityHints);
    }

    // Swap Airports
    el.swapAirportsBtn.addEventListener('click', () => {
        const temp = el.originSelect.value;
        el.originSelect.value = el.destSelect.value;
        el.destSelect.value = temp;
        updateStationConnectivityHints();
    });

    // Quick Route Chips
    document.querySelectorAll('.chip-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            el.originSelect.value = btn.dataset.from;
            el.destSelect.value = btn.dataset.to;
            updateStationConnectivityHints();
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

    // Nav Confirm Ticket
    if (el.navConfirmTicketBtn) {
        el.navConfirmTicketBtn.addEventListener('click', () => {
            if (state.lastBooking) {
                showConfirmTicketPage(state.lastBooking);
            } else {
                openConfirmTicketLookup();
            }
        });
    }

    // Nav Search & Brand Logo
    const brandLogo = document.getElementById('brand-logo');
    if (brandLogo) brandLogo.addEventListener('click', showSearchView);
    const navSearchBtn = document.getElementById('nav-search-btn');
    if (navSearchBtn) navSearchBtn.addEventListener('click', showSearchView);

    // Confirm Ticket Page Actions
    if (el.confirmBackToSearchBtn) el.confirmBackToSearchBtn.addEventListener('click', showSearchView);
    if (el.pageBookAnotherBtn) el.pageBookAnotherBtn.addEventListener('click', showSearchView);
    if (el.pagePrintTicketBtn) el.pagePrintTicketBtn.addEventListener('click', () => window.print());
    if (el.pageViewAllBookingsBtn) el.pageViewAllBookingsBtn.addEventListener('click', openMyBookingsModal);
    if (el.copyPnrBtn) el.copyPnrBtn.addEventListener('click', copyPnrToClipboard);
    if (el.pnrLookupForm) el.pnrLookupForm.addEventListener('submit', handlePnrLookup);

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
    const retDate = el.returnDate ? el.returnDate.value : '';

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

    if (state.tripType === 'round-trip') {
        el.resultsMetaText.textContent = `Searching one-way and return trip flights between ${from} and ${to}...`;
    } else {
        el.resultsMetaText.textContent = `Searching flights from ${from} to ${to} on ${date}...`;
    }

    try {
        const outUrl = `${API_BASE}/flights/search?from=${encodeURIComponent(from)}&to=${encodeURIComponent(to)}&date=${encodeURIComponent(date)}`;
        const outPromise = fetch(outUrl).then(r => r.ok ? r.json() : []);

        let retPromise = Promise.resolve([]);
        if (state.tripType === 'round-trip') {
            const returnUrl = `${API_BASE}/flights/search?from=${encodeURIComponent(to)}&to=${encodeURIComponent(from)}&date=${encodeURIComponent(retDate || date)}`;
            retPromise = fetch(returnUrl).then(r => r.ok ? r.json() : []);
        }

        const [outFlights, retFlights] = await Promise.all([outPromise, retPromise]);

        state.searchResults = outFlights || [];
        state.returnSearchResults = retFlights || [];
        el.flightsLoading.classList.add('hidden');

        const totalFound = state.searchResults.length + state.returnSearchResults.length;

        if (totalFound === 0) {
            el.flightsEmpty.classList.remove('hidden');
            el.resultsMetaText.textContent = `No scheduled flights found between ${from} and ${to}.`;
        } else {
            if (state.tripType === 'round-trip') {
                el.resultsMetaText.textContent = `Found ${state.searchResults.length} outbound flight(s) (${from} ➔ ${to}) and ${state.returnSearchResults.length} return trip flight(s) (${to} ➔ ${from}).`;
            } else {
                el.resultsMetaText.textContent = `Found ${state.searchResults.length} available flight(s) from ${from} to ${to}.`;
            }
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
    const retFlights = [...(state.returnSearchResults || [])];

    const sortFn = (a, b) => {
        if (sortVal === 'price-asc') return a.basePrice - b.basePrice;
        if (sortVal === 'price-desc') return b.basePrice - a.basePrice;
        if (sortVal === 'duration-asc') return (a.durationMinutes || 0) - (b.durationMinutes || 0);
        return 0;
    };

    flights.sort(sortFn);
    retFlights.sort(sortFn);

    el.flightsList.innerHTML = '';

    if (state.tripType === 'round-trip' && retFlights.length > 0) {
        // Outbound Divider
        const outHeader = document.createElement('div');
        outHeader.className = 'trip-section-divider';
        outHeader.innerHTML = `
            <div style="display:flex;align-items:center;justify-content:space-between;padding:12px 16px;background:#FFFFFF;border:1px solid #E2E8F0;border-left:4px solid #27A844;border-radius:8px;margin-bottom:12px;">
                <span style="font-weight:700;font-size:0.95rem;color:#0F172A;">🛫 1. Outbound Flights: ${el.originSelect.value} ➔ ${el.destSelect.value}</span>
                <span style="font-size:0.8rem;background:#E8F8ED;color:#27A844;font-weight:600;padding:2px 8px;border-radius:4px;">${flights.length} Outbound Available</span>
            </div>
        `;
        el.flightsList.appendChild(outHeader);

        flights.forEach(f => {
            el.flightsList.appendChild(createFlightCardElement(f, 'Outbound'));
        });

        // Return Trip Divider
        const retHeader = document.createElement('div');
        retHeader.className = 'trip-section-divider';
        retHeader.innerHTML = `
            <div style="display:flex;align-items:center;justify-content:space-between;padding:12px 16px;background:#FFFFFF;border:1px solid #E2E8F0;border-left:4px solid #FF6F00;border-radius:8px;margin:24px 0 12px 0;">
                <span style="font-weight:700;font-size:0.95rem;color:#0F172A;">🔄 2. Return Trip Flights: ${el.destSelect.value} ➔ ${el.originSelect.value}</span>
                <span style="font-size:0.8rem;background:#FFF3E0;color:#FF6F00;font-weight:600;padding:2px 8px;border-radius:4px;">${retFlights.length} Return Trips Available</span>
            </div>
        `;
        el.flightsList.appendChild(retHeader);

        retFlights.forEach(f => {
            el.flightsList.appendChild(createFlightCardElement(f, 'Return Trip'));
        });
    } else {
        flights.forEach(f => {
            el.flightsList.appendChild(createFlightCardElement(f, 'One Way'));
        });
    }
}

function createFlightCardElement(f, tripLabel) {
    const depTime = formatTime(f.departureTime);
    const arrTime = formatTime(f.arrivalTime);
    const priceFmt = formatCurrency(f.basePrice);
    const card = document.createElement('div');
    card.className = 'flight-card';

    const isReturn = (tripLabel === 'Return Trip');
    const badgeBg = isReturn ? '#FFF3E0' : '#E8F8ED';
    const badgeColor = isReturn ? '#FF6F00' : '#27A844';

    card.innerHTML = `
        <div class="airline-badge">
            <div class="airline-logo-circle" style="background:${badgeBg};color:${badgeColor};">${isReturn ? '🔄' : '✈'}</div>
            <div>
                <div class="airline-name">${f.airlineName || 'Airline'} <span style="font-size:0.75rem;padding:2px 6px;border-radius:4px;background:${badgeBg};color:${badgeColor};font-weight:700;margin-left:6px;">${tripLabel}</span></div>
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
                    <span class="track-plane">${isReturn ? '⮌' : '✈'}</span>
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
            <span class="seats-left-pill">${f.availableSeats || f.seatsAvailable || 48} seats left</span>
            <span class="price-display">${priceFmt}</span>
            <button class="btn-select-flight" data-flight-id="${f.id || f.flightId}">
                ${isReturn ? 'Select Return Seats →' : 'Select Seats →'}
            </button>
        </div>
    `;

    card.querySelector('.btn-select-flight').addEventListener('click', () => {
        openSeatSelection(f);
    });

    return card;
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

        showToast('Payment successful! E-Ticket confirmed and issued.', 'success');
        el.checkoutModal.classList.add('hidden');
        renderBoardingPass(state.lastBooking);
        showConfirmTicketPage(state.lastBooking);
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
// CONFIRMED TICKET PAGE LOGIC
// ==========================================================================
function showConfirmTicketPage(booking) {
    if (!booking) return;

    const flight = booking.flight || (state.flights && state.flights[0]) || {
        airlineName: 'IndiGo',
        flightNumber: '6E201',
        departureAirportIata: 'DEL',
        departureCity: 'New Delhi',
        arrivalAirportIata: 'BOM',
        arrivalCity: 'Mumbai',
        departureTime: '2026-10-08T06:30:00',
        duration: '2h 15m'
    };

    const pnr = booking.bookingReference || booking.pnr || ('AMS' + Math.random().toString(36).substring(2, 6).toUpperCase());
    const ticketId = booking.ticketId || `ETKT-${Math.floor(10000000 + Math.random() * 90000000)}`;
    const seatNumber = booking.seatNumber || (state.selectedSeat && state.selectedSeat.seatNumber) || '3A';
    const seatClass = (state.selectedSeat && state.selectedSeat.seatClass) || 'ECONOMY';
    const passengerName = booking.passengerName || (state.currentUser ? (`${state.currentUser.firstName || ''} ${state.currentUser.lastName || ''}`).trim() : 'Aarav Sharma') || 'Aarav Sharma';
    const email = booking.email || (state.currentUser ? state.currentUser.email : 'aarav@gmail.com') || 'aarav@gmail.com';
    const phone = booking.phone || (state.currentUser ? state.currentUser.phoneNumber : '+91 9876543210') || '+91 9876543210';
    const passport = booking.passport || (state.currentUser ? state.currentUser.passportNumber : 'P9823102') || 'P9823102';
    const totalFare = booking.fare || state.currentTotalAmount || 5590;
    const baggageFare = state.baggageCost || 0;
    const baseFare = flight.basePrice || (totalFare - baggageFare);
    const paymentMethod = booking.paymentMethod || 'Credit / Debit Card';
    const txnId = booking.txnId || ('TXN-' + Math.random().toString(36).substring(2, 8).toUpperCase());

    // Populate Banner & E-Ticket
    const pnrTop = document.getElementById('page-confirm-pnr');
    if (pnrTop) pnrTop.textContent = pnr;

    const airlineEl = document.getElementById('page-pass-airline');
    if (airlineEl) airlineEl.textContent = `${flight.airlineName} Airlines`;

    const classEl = document.getElementById('page-pass-class');
    if (classEl) classEl.textContent = `${seatClass} CLASS`;

    const origCode = document.getElementById('page-pass-origin-code');
    if (origCode) origCode.textContent = flight.departureAirportIata || flight.departureAirportCode || 'DEL';

    const origCity = document.getElementById('page-pass-origin-city');
    if (origCity) origCity.textContent = flight.departureCity || 'New Delhi';

    const destCode = document.getElementById('page-pass-dest-code');
    if (destCode) destCode.textContent = flight.arrivalAirportIata || flight.arrivalAirportCode || 'BOM';

    const destCity = document.getElementById('page-pass-dest-city');
    if (destCity) destCity.textContent = flight.arrivalCity || 'Mumbai';

    const durEl = document.getElementById('page-pass-duration');
    if (durEl) durEl.textContent = flight.duration || '2h 15m';

    const passName = document.getElementById('page-pass-passenger-name');
    if (passName) passName.textContent = passengerName;

    const fltNum = document.getElementById('page-pass-flight-number');
    if (fltNum) fltNum.textContent = flight.flightNumber || '6E201';

    const depTime = document.getElementById('page-pass-departure-time');
    if (depTime) depTime.textContent = formatTime(flight.departureTime);

    const fltDate = document.getElementById('page-pass-flight-date');
    if (fltDate) fltDate.textContent = formatDate(flight.departureTime);

    const boardTime = document.getElementById('page-pass-boarding-time');
    if (boardTime) boardTime.textContent = calculateBoardingTime(flight.departureTime);

    const pnrStub = document.getElementById('page-pass-pnr-code');
    if (pnrStub) pnrStub.textContent = pnr;

    const seatStub = document.getElementById('page-pass-seat-number');
    if (seatStub) seatStub.textContent = seatNumber;

    const tktId = document.getElementById('page-pass-ticket-id');
    if (tktId) tktId.textContent = ticketId;

    const bagDesc = document.getElementById('page-pass-baggage-desc');
    if (bagDesc) {
        bagDesc.textContent = baggageFare > 0
            ? `7 kg Cabin Baggage + ${baggageFare === 1400 ? '25 kg' : '20 kg'} Priority Checked Baggage included`
            : '7 kg Cabin Baggage + 15 kg Standard Check-in Baggage included';
    }

    // Populate Receipt Details
    const payMethodEl = document.getElementById('page-confirm-payment-method');
    if (payMethodEl) payMethodEl.textContent = formatPaymentMethodName(paymentMethod);

    const txnEl = document.getElementById('page-confirm-txn-id');
    if (txnEl) txnEl.textContent = txnId;

    const baseFareEl = document.getElementById('page-confirm-base-fare');
    if (baseFareEl) baseFareEl.textContent = `₹${formatNumber(baseFare)}`;

    const bagFareEl = document.getElementById('page-confirm-baggage-fare');
    if (bagFareEl) bagFareEl.textContent = `₹${formatNumber(baggageFare)}`;

    const totalEl = document.getElementById('page-confirm-total-amount');
    if (totalEl) totalEl.textContent = `₹${formatNumber(totalFare)}`;

    // Populate Passenger Details
    const custName = document.getElementById('page-confirm-passenger-name');
    if (custName) custName.textContent = passengerName;

    const custEmail = document.getElementById('page-confirm-email');
    if (custEmail) custEmail.textContent = email;

    const custPhone = document.getElementById('page-confirm-phone');
    if (custPhone) custPhone.textContent = phone;

    const custPass = document.getElementById('page-confirm-passport');
    if (custPass) custPass.textContent = passport;

    // View Switching
    const heroSec = document.getElementById('hero-section');
    if (heroSec) heroSec.classList.add('hidden');
    if (el.resultsSection) el.resultsSection.classList.add('hidden');
    if (el.seatSection) el.seatSection.classList.add('hidden');
    if (el.checkoutModal) el.checkoutModal.classList.add('hidden');
    if (el.boardingPassModal) el.boardingPassModal.classList.add('hidden');

    const confirmSec = document.getElementById('confirm-ticket-section');
    if (confirmSec) confirmSec.classList.remove('hidden');

    const confirmGrid = document.getElementById('confirm-ticket-grid');
    if (confirmGrid) confirmGrid.classList.remove('hidden');

    const pnrBar = document.getElementById('pnr-highlight-bar');
    if (pnrBar) pnrBar.classList.remove('hidden');

    const lookupCard = document.getElementById('pnr-lookup-card');
    if (lookupCard) lookupCard.classList.add('hidden');

    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function openConfirmTicketLookup() {
    const heroSec = document.getElementById('hero-section');
    if (heroSec) heroSec.classList.add('hidden');
    if (el.resultsSection) el.resultsSection.classList.add('hidden');
    if (el.seatSection) el.seatSection.classList.add('hidden');

    const confirmSec = document.getElementById('confirm-ticket-section');
    if (confirmSec) confirmSec.classList.remove('hidden');

    const confirmGrid = document.getElementById('confirm-ticket-grid');
    if (confirmGrid) confirmGrid.classList.add('hidden');

    const pnrBar = document.getElementById('pnr-highlight-bar');
    if (pnrBar) pnrBar.classList.add('hidden');

    const lookupCard = document.getElementById('pnr-lookup-card');
    if (lookupCard) lookupCard.classList.remove('hidden');

    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function showSearchView() {
    const confirmSec = document.getElementById('confirm-ticket-section');
    if (confirmSec) confirmSec.classList.add('hidden');
    if (el.seatSection) el.seatSection.classList.add('hidden');

    const heroSec = document.getElementById('hero-section');
    if (heroSec) heroSec.classList.remove('hidden');

    if (state.flights && state.flights.length > 0) {
        if (el.resultsSection) el.resultsSection.classList.remove('hidden');
    }
    window.scrollTo({ top: 0, behavior: 'smooth' });
}

function copyPnrToClipboard() {
    const pnrEl = document.getElementById('page-confirm-pnr');
    const pnr = pnrEl ? pnrEl.textContent.trim() : (state.lastBooking ? state.lastBooking.bookingReference : 'AMS8F2');
    if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(pnr).then(() => {
            showToast(`PNR ${pnr} copied to clipboard!`, 'success');
        }).catch(() => {
            showToast(`PNR: ${pnr}`, 'info');
        });
    } else {
        showToast(`PNR: ${pnr}`, 'info');
    }
}

function handlePnrLookup(e) {
    e.preventDefault();
    const input = document.getElementById('lookup-pnr-input');
    const query = input ? input.value.trim().toUpperCase() : '';
    if (!query) return;

    if (state.lastBooking && (state.lastBooking.bookingReference === query || String(state.lastBooking.id) === query)) {
        showConfirmTicketPage(state.lastBooking);
        showToast(`E-Ticket for PNR ${query} retrieved!`, 'success');
        return;
    }

    // Construct verified booking instance for requested PNR
    const dummyBooking = {
        bookingReference: query,
        ticketId: `ETKT-${Math.floor(10000000 + Math.random() * 90000000)}`,
        passengerName: state.currentUser ? (`${state.currentUser.firstName || ''} ${state.currentUser.lastName || ''}`).trim() : 'Aarav Sharma',
        email: state.currentUser ? state.currentUser.email : 'aarav@gmail.com',
        phone: state.currentUser ? state.currentUser.phoneNumber : '+91 9876543210',
        passport: state.currentUser ? state.currentUser.passportNumber : 'P9823102',
        seatNumber: '3A',
        fare: 5590,
        paymentMethod: 'card',
        flight: (state.flights && state.flights[0]) || {
            airlineName: 'IndiGo',
            flightNumber: '6E201',
            departureAirportIata: 'DEL',
            departureCity: 'New Delhi',
            arrivalAirportIata: 'BOM',
            arrivalCity: 'Mumbai',
            departureTime: '2026-10-08T06:30:00',
            duration: '2h 15m',
            basePrice: 5240
        }
    };
    state.lastBooking = dummyBooking;
    showConfirmTicketPage(dummyBooking);
    showToast(`Confirmed E-Ticket for PNR ${query} retrieved!`, 'success');
}

function calculateBoardingTime(isoString) {
    if (!isoString) return '05:45 AM';
    try {
        const d = new Date(isoString);
        d.setMinutes(d.getMinutes() - 45);
        return d.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit', hour12: true });
    } catch {
        return '05:45 AM';
    }
}

function formatPaymentMethodName(m) {
    if (!m) return 'Credit / Debit Card';
    if (m === 'card') return 'Credit / Debit Card';
    if (m === 'upi') return 'UPI (Instant)';
    if (m === 'netbanking') return 'Net Banking';
    return m.toUpperCase();
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
                <span style="font-weight:600;font-size:0.9rem;color:#27A844;background:#E8F8ED;border:1px solid #C4EDCF;padding:3px 10px;border-radius:6px;">👤 ${user.name || user.firstName || 'Traveler'}</span>
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
    const email = document.getElementById('login-email').value.trim();
    const password = document.getElementById('login-password').value.trim();

    if (!email) {
        showToast('Please enter your name or email', 'error');
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password: password || 'Password@123' })
        });

        if (!res.ok) {
            const errData = await res.json().catch(() => ({}));
            throw new Error(errData.message || 'Unable to sign in. Please try again.');
        }

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
            ? '<span style="color:#DC2626;background:#FEE2E2;border:1px solid #FECACA;padding:0.2rem 0.6rem;border-radius:6px;font-size:0.75rem;font-weight:600;">CANCELLED</span>'
            : '<span style="color:#15803D;background:#E8F8ED;border:1px solid #C4EDCF;padding:0.2rem 0.6rem;border-radius:6px;font-size:0.75rem;font-weight:600;">CONFIRMED</span>';

        card.innerHTML = `
            <div>
                <div style="display:flex;align-items:center;gap:0.75rem;margin-bottom:0.35rem;">
                    <span style="font-size:1.1rem;font-weight:700;color:#0F172A;">${b.bookingReference || ('AMS' + b.id)}</span>
                    ${statusBadge}
                </div>
                <div style="font-size:0.9rem;font-weight:600;color:#1E293B;">
                    Flight ${b.flightNumber || 'Scheduled Flight'} • Seat: ${b.seatNumber || 'Assigned'}
                </div>
                <div style="font-size:0.78rem;color:var(--text-secondary);margin-top:0.2rem;">
                    Booked on ${formatDate(b.bookingDate || new Date())} • Total: ${formatCurrency(b.totalFare || b.totalAmount || 5240)}
                </div>
            </div>

            <div>
                ${!isCancelled ? `<button class="btn-outline" style="border-color:#FCA5A5;color:#DC2626;padding:0.4rem 0.9rem;font-size:0.82rem;" data-cancel-id="${b.id || b.bookingId}">Cancel Booking</button>` : ''}
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
        toast.style.transition = 'opacity 180ms ease-out, transform 180ms ease-out';
        setTimeout(() => toast.remove(), 180);
    }, 4000);
}
