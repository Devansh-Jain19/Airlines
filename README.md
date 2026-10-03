# ✈️ Airlines Flight Reservation & Management System

> **Enterprise Full-Stack Aviation Reservation Platform**  
> Built with **Android (Java / MVVM / Material Design 3)**, **Spring Boot 3.x (Java 17+ / JPA / Hibernate)**, and **MySQL 8.0+ (3NF Relational Architecture)**.

---

## 📖 Executive Summary

The **Airlines Flight Reservation System** is an end-to-end airline booking, seat allocation, and passenger management platform. Designed to simulate real-world aviation workflows, the system bridges an interactive native mobile application with an enterprise-grade RESTful backend and a strictly normalized relational database.

From searching multi-airline flight routes and selecting specific aircraft cabin seats to authorizing payments, generating digital boarding passes with barcodes, and executing cancellations, the platform delivers a production-grade experience adhering to modern software engineering principles.

---

## 👥 The Team & Individual Contributions

This project was collaboratively engineered by three specialized team members, each taking end-to-end ownership of an architectural layer:

```
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                                   THE CORE TEAM                                        │
├──────────────────────────────┬─────────────────────────────┬───────────────────────────┤
│    Hardik                    │   Devansh                   │   Chirag                  │
│    `feat/backend-hardik`     │   `frontend-Devansh`        │   `db_chirag`             │
│    Backend Lead & Integrator │   Mobile Frontend Lead      │   Database Lead & Arch    │
└──────────────────────────────┴─────────────────────────────┴───────────────────────────┘
```

---

### 👤 Hardik (`feat/backend-hardik`) — Backend Lead & Full-Stack System Integrator *(Project Lead)*
**Branch:** [`feat/backend-hardik`](https://github.com/hardik760/Airlines/tree/feat/backend-hardik)  
**Primary Domain:** Spring Boot 3 REST API Architecture, Business Logic, System Integration, Security & DevOps.

#### Key Deliverables & Technical Contributions:
- **Spring Boot 3.x Architecture:**
  - Designed and implemented the complete REST API service utilizing Spring Boot 3.2.2 and Java 17/24.
  - Implemented clean layered architecture: `Controller` ➡️ `Service` ➡️ `Repository` ➡️ `Entity` with strict separation of concerns.
  - Created domain controllers: [`AuthController`](file:///Users/hardik/Airlines/backend/src/main/java/com/airline/controller/AuthController.java), [`FlightController`](file:///Users/hardik/Airlines/backend/src/main/java/com/airline/controller/FlightController.java), [`BookingController`](file:///Users/hardik/Airlines/backend/src/main/java/com/airline/controller/BookingController.java), [`PaymentController`](file:///Users/hardik/Airlines/backend/src/main/java/com/airline/controller/PaymentController.java), [`TicketController`](file:///Users/hardik/Airlines/backend/src/main/java/com/airline/controller/TicketController.java), [`AirportController`](file:///Users/hardik/Airlines/backend/src/main/java/com/airline/controller/AirportController.java), and [`PassengerController`](file:///Users/hardik/Airlines/backend/src/main/java/com/airline/controller/PassengerController.java).
- **Core Aviation Business Logic:**
  - **Seat Allocation & Concurrency Safety:** Engineered transactional seat booking logic in [`BookingService.java`](file:///Users/hardik/Airlines/backend/src/main/java/com/airline/service/BookingService.java) ensuring that multiple passengers cannot reserve the same seat simultaneously (preventing race conditions and double bookings).
  - **Booking Reference Generator:** Developed unique alphanumeric PNR (Passenger Name Record) reference code generation (e.g. `BK8F29A`).
  - **Dynamic Fare Engine:** Automated multi-class pricing algorithms calculating base fare, seat class premiums (Business vs Economy), baggage surcharges, and statutory taxes.
  - **Booking Cancellation Engine:** Built two-phase cancellation lifecycle in [`BookingService`](file:///Users/hardik/Airlines/backend/src/main/java/com/airline/service/BookingService.java) and [`TicketService`](file:///Users/hardik/Airlines/backend/src/main/java/com/airline/service/TicketService.java) that transitions tickets to `CANCELLED`, frees seat assignments for other travelers, and adjusts booking status.
- **Security & Global Exception Framework:**
  - Implemented [`GlobalExceptionHandler.java`](file:///Users/hardik/Airlines/backend/src/main/java/com/airline/exception/GlobalExceptionHandler.java) returning standardized [`ApiErrorDto`](file:///Users/hardik/Airlines/backend/src/main/java/com/airline/dto/response/ApiErrorDto.java) responses across all endpoints.
  - Handled custom domain exceptions: `SeatUnavailableException`, `PaymentFailedException`, and `ResourceNotFoundException`.
  - Configured [`SecurityConfig.java`](file:///Users/hardik/Airlines/backend/src/main/java/com/airline/config/SecurityConfig.java) with `BCryptPasswordEncoder` for cryptographic password hashing and CORS filters allowing seamless Android mobile connectivity.
- **Automated Data Seeding:**
  - Created [`DataInitializer.java`](file:///Users/hardik/Airlines/backend/src/main/java/com/airline/config/DataInitializer.java) (`CommandLineRunner`) providing boot-time automated data provisioning: pre-seeding 10 international airports, 4 airlines, aircraft cabins, 4-column seat grids, real scheduled flights, and demo traveler profiles.
- **Full-Stack Integration & Diagnostics:**
  - Diagnosed and resolved local network port conflict by migrating backend default port to `8081` and updating the Android [`RetrofitClient`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/network/RetrofitClient.java).
  - Fixed compilation errors in Android repository layers and adapters (`FlightRepository`, `BookingHistoryAdapter`).
  - Added robust unit and mock integration test suites ([`AirportAndCrossCuttingTest`](file:///Users/hardik/Airlines/backend/src/test/java/com/airline/controller/AirportAndCrossCuttingTest.java), [`AuthControllerTest`](file:///Users/hardik/Airlines/backend/src/test/java/com/airline/controller/AuthControllerTest.java), [`FlightControllerTest`](file:///Users/hardik/Airlines/backend/src/test/java/com/airline/controller/FlightControllerTest.java), [`BookingPaymentControllerTest`](file:///Users/hardik/Airlines/backend/src/test/java/com/airline/controller/BookingPaymentControllerTest.java)).
  - Authored team Git standard development procedures in [`devlog.md`](file:///Users/hardik/Airlines/devlog.md) and created the beginner setup manual [`SETUP.md`](file:///Users/hardik/Airlines/SETUP.md).

---

### 👤 Devansh (`frontend-Devansh`) — Mobile Frontend Lead & Android Engineer
**Branch:** [`frontend-Devansh`](https://github.com/hardik760/Airlines/tree/frontend-Devansh)  
**Primary Domain:** Native Android Application, MVVM Architecture, Jetpack UI/UX, Retrofit Networking.

#### Key Deliverables & Technical Contributions:
- **Native Android UI/UX Implementation (Material Design 3):**
  - Designed and built responsive, clean layouts with Android ViewBinding and XML styles.
  - **Authentication Flows:** Built [`LoginActivity`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/ui/auth/LoginActivity.java) and [`RegisterActivity`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/ui/auth/RegisterActivity.java) with real-time field validation, error highlights, and password visibility toggles.
  - **Flight Search & Discovery:** Built [`MainActivity`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/ui/search/MainActivity.java) with Bottom Navigation hosting [`SearchFragment`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/ui/search/SearchFragment.java), featuring interactive date pickers, airport swap animations, and popular destinations quick-chips.
  - **Flight Search Results:** Built [`FlightListActivity`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/ui/flights/FlightListActivity.java) with sorting, airline logos, flight duration calculation, and fallback zero-state handlers.
  - **Interactive Aircraft Cabin Seat Selection:** Engineered [`SeatSelectionActivity`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/ui/seats/SeatSelectionActivity.java) featuring a 4-column aircraft aisle layout (**A - B [Aisle] C - D**). Supports live dynamic state indicators (`Available`, `Occupied`, `Selected`) with pricing breakdown between Business and Economy cabins.
  - **Multi-Step Checkout & Payment:** Built [`CheckoutActivity`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/ui/booking/CheckoutActivity.java) and [`PaymentBottomSheet`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/ui/booking/PaymentBottomSheet.java) supporting Credit/Debit, UPI, and Net Banking payment simulation.
  - **Digital Boarding Pass:** Implemented [`BoardingPassActivity`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/ui/ticket/BoardingPassActivity.java) rendering dynamic passenger tickets with PNR codes, flight metadata, and scannable visual barcodes.
  - **Trip History & Management:** Built [`MyBookingsActivity`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/ui/ticket/MyBookingsActivity.java) allowing passengers to inspect active/completed bookings and execute real-time trip cancellations.
- **MVVM Architecture & Reactive Data Layer:**
  - Implemented ViewModels ([`AuthViewModel`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/viewmodel/AuthViewModel.java), [`FlightViewModel`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/viewmodel/FlightViewModel.java), [`BookingViewModel`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/viewmodel/BookingViewModel.java)) observing `LiveData` to survive device configuration changes (screen rotations).
  - Built Repository pattern ([`AuthRepository`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/repository/AuthRepository.java), [`FlightRepository`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/repository/FlightRepository.java), [`BookingRepository`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/repository/BookingRepository.java)) abstracting networking operations.
  - Authored generic [`Resource<T>`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/network/Resource.java) reactive state wrapper encapsulating `LOADING`, `SUCCESS`, and `ERROR` states.
- **Networking & Persistence:**
  - Set up **Retrofit 2** singleton ([`RetrofitClient`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/network/RetrofitClient.java)) with OkHttp `HttpLoggingInterceptor` for real-time HTTP payload debugging.
  - Implemented [`SessionManager`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/util/SessionManager.java) leveraging Android `SharedPreferences` for persistent user authentication sessions.
  - Authored modular RecyclerView adapters ([`FlightAdapter`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/ui/flights/FlightAdapter.java), [`SeatGridAdapter`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/ui/seats/SeatGridAdapter.java), [`AirportAdapter`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/ui/search/AirportAdapter.java), [`BookingHistoryAdapter`](file:///Users/hardik/Airlines/app/src/main/java/com/airline/app/ui/ticket/BookingHistoryAdapter.java)).
  - Authored comprehensive frontend architectural guide in [`frontend.md`](file:///Users/hardik/Airlines/frontend.md).

---

### 👤 Chirag (`db_chirag`) — Database Lead & Data Architect
**Branch:** [`db_chirag`](https://github.com/hardik760/Airlines/tree/db_chirag)  
**Primary Domain:** Relational Schema Architecture (3NF), DDL/DML, Constraints, Query Optimization, Analytical Views.

#### Key Deliverables & Technical Contributions:
- **Relational Schema Design in Third Normal Form (3NF):**
  - Architected 12 interconnected relational tables in [`schema.sql`](file:///Users/hardik/Airlines/schema.sql):
    1. `airline`: Carrier metadata and unique 2-letter IATA codes.
    2. `airport`: Global airports with unique 3-letter IATA identifiers.
    3. `aircraft_model`: Manufacturer, model specifications, and certified flight ranges.
    4. `aircraft`: Physical fleet tracking with registration tail numbers and maintenance statuses.
    5. `seat`: Cabin seat layout per aircraft (`A-D` seat configurations with class classifications).
    6. `flight`: Scheduled routes with origin, destination, timestamps, and base pricing.
    7. `passenger`: User profile records with passport validation and cryptographic password storage.
    8. `booking`: PNR tracking, booking timestamps, and overall booking statuses.
    9. `payment`: Financial transaction logs, payment methods, transaction references, and statuses.
    10. `ticket`: E-ticket records binding passenger, booking, flight, and seat with unique ticket numbers.
    11. `baggage`: Luggage tracking linked directly to issued tickets with weight constraints.
    12. `employee`: Airline staff records, roles, departments, and active statuses.
- **Relational Integrity & Constraint Enforcement:**
  - Implemented native MySQL 8.0 `CHECK` constraints ensuring domain correctness (`range_km > 0`, `manufacture_year BETWEEN 1990 AND 2035`, `status IN (...)`, `arrival_time > departure_time`, `base_price > 0`).
  - Configured foreign key cascade policies (`ON DELETE RESTRICT`) preventing orphaned flight and booking dependencies.
- **Indexing & High-Performance Views:**
  - Designed composite performance indexes in [`indexes.sql`](file:///Users/hardik/Airlines/indexes.sql) optimizing multi-column searches (`flight(departure_airport_id, arrival_airport_id, departure_time)`).
  - Built reusable analytical and transactional SQL views in [`views.sql`](file:///Users/hardik/Airlines/views.sql):
    - `v_flight_schedule`: Consolidated flight itineraries with airport names and airline branding.
    - `v_booking_total`: Calculated aggregated invoice totals per booking.
    - `v_flight_occupancy`: Real-time aircraft cabin seat utilization metrics.
- **Comprehensive Dataset & Analytical Query Suite:**
  - Populated over 80+ realistic mock records per core table in [`seed.sql`](file:///Users/hardik/Airlines/seed.sql).
  - Formulated 20+ analytical business queries in [`queries.sql`](file:///Users/hardik/Airlines/queries.sql) analyzing airline route revenue, frequent flyer metrics, load factors, and seat vacancy.
  - Authored database audit and technical specifications in [`db.md`](file:///Users/hardik/Airlines/db.md), [`database_review.md`](file:///Users/hardik/Airlines/database_review.md), and [`database_api_reference.md`](file:///Users/hardik/Airlines/database_api_reference.md).

---

## 🏛️ System Architecture & Data Flow

```
┌────────────────────────────────────────────────────────────────────────┐
│                        PRESENTATION TIER (Android)                     │
│  Activities / Fragments / XML ViewBinding / Material Components 3      │
│  MVVM: AuthViewModel | FlightViewModel | BookingViewModel             │
│  State Wrapper: Resource<T> (LOADING | SUCCESS | ERROR)               │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ HTTP / JSON (Port 8081)
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        APPLICATION TIER (Spring Boot 3)                │
│  REST API Controllers (Auth, Flight, Booking, Payment, Ticket)        │
│  Service Business Layer (Seat Locking, PNR Generation, Fare Engine)    │
│  Security & Exception Handling (GlobalExceptionHandler, BCrypt)        │
│  Spring Data JPA Repositories                                          │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ JDBC / Hibernate SQL (Port 3306)
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                         DATA TIER (MySQL 8.0 / H2)                     │
│  12 Normalized 3NF Tables | Strict Foreign Keys & CHECK Constraints    │
│  Composite B-Tree Indexes | Dynamic Analytical SQL Views               │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 💡 Key Architectural Insights & Engineering Decisions

1. **Why MVVM on Android?**
   - Traditional Android activities mix UI layout and business logic, causing crashes during screen rotations and low memory. MVVM decouples view rendering from state management using `ViewModel` and `LiveData`, ensuring persistent UI state and clean unit testing.
2. **Double-Booking Race Condition Prevention:**
   - In airline reservation systems, concurrent requests for the same seat present a critical race condition. Hardik engineered transactional checks within `BookingService.java` that verify seat availability within a single database transaction, preventing duplicate ticket issuance.
3. **Emulator Loopback Bridge (`10.0.2.2`):**
   - Android Emulators operate inside a virtual router isolated from the host machine. While desktop apps connect via `localhost`, Android emulators must route to `10.0.2.2`. This was formalized in `RetrofitClient.java` with configurable overrides for physical device LAN IPs.
4. **Port 8081 Strategy:**
   - Standard web servers (like Apache, Nginx, or local proxies) default to port `8080`. To eliminate frequent startup failures for developers and graders, the backend was explicitly migrated to port `8081` with corresponding Android endpoints synchronized.
5. **Database Normalization (3NF) vs Performance:**
   - Aircraft models are decoupled from individual physical aircraft (`aircraft_model` vs `aircraft`), eliminating redundant storage of technical specs. Cabin seats are linked to physical aircraft rather than flight numbers, allowing individual aircraft to be dynamically reassigned to different flights without schema corruption.

---

## 🌟 Feature Highlights

| Category | Features |
| :--- | :--- |
| **Authentication** | Passenger registration with passport validation, encrypted password storage (`BCrypt`), session management (`SharedPreferences`). |
| **Flight Search** | Origin/destination airport autocomplete, departure date selection, reverse airport swap, live route filtering. |
| **Seat Map** | Visual 4-column aircraft aisle layout, cabin class separation (Business/Economy), real-time seat availability states. |
| **Checkout & Fare** | Dynamic price calculation, check-in baggage weight options, transparent fare breakdown with taxes. |
| **Payment Gateway** | Bottom sheet payment modal, mock authorization for Credit Cards, UPI, and Net Banking. |
| **Boarding Pass** | Generated digital flight ticket with passenger name, PNR reference, gate, seat number, and barcode. |
| **Trip Management** | Past and upcoming trip history in "My Bookings", real-time cancellation workflow with seat deallocation. |
| **Analytics & BI** | SQL views and queries calculating fleet load factor, popular routes, and revenue per carrier. |

---

## 🚀 Getting Started (Quick Setup)

For the **detailed, step-by-step setup guide for beginners**, please read:
👉 **[`SETUP.md`](file:///Users/hardik/Airlines/SETUP.md)**

### Quickstart Summary:

#### 1. Start Backend (Zero-Configuration with Built-in Seed Data)
```bash
cd backend
./mvnw spring-boot:run
```
*Backend starts on `http://localhost:8081` with in-memory MySQL compatibility and pre-seeded flights!*

#### 2. Verify Backend
```bash
curl http://localhost:8081/api/airports
```

#### 3. Run Android App
1. Open the repository root folder in **Android Studio**.
2. Wait for Gradle Sync to complete.
3. Launch an Android Emulator (Pixel with API 33/34).
4. Click **Run 'app'** (`Shift + F10`).
5. Log in with demo account `aarav@gmail.com` / `Password@123` or register a new traveler!

---

## 🔌 Core REST API Endpoints

| Domain | Method | Endpoint | Description |
| :--- | :--- | :--- | :--- |
| **Auth** | `POST` | `/api/auth/register` | Register a new passenger profile |
| **Auth** | `POST` | `/api/auth/login` | Authenticate traveler and return session DTO |
| **Airports** | `GET` | `/api/airports` | List all airports for autocomplete search |
| **Flights** | `GET` | `/api/flights/search` | Search flights by origin, destination, and departure date |
| **Flights** | `GET` | `/api/flights/{flightId}/seats` | Fetch cabin seat layout and current reservation status |
| **Bookings** | `POST` | `/api/bookings` | Create new flight booking with passenger and seat locking |
| **Bookings** | `GET` | `/api/bookings/passenger/{passengerId}` | Retrieve trip history for a specific passenger |
| **Bookings** | `POST` | `/api/bookings/{bookingId}/cancel` | Cancel existing reservation and release seats |
| **Payments** | `POST` | `/api/payments` | Process and authorize booking payment |
| **Tickets** | `GET` | `/api/tickets/booking/{bookingId}` | Retrieve issued e-tickets and boarding pass data |

---

## 🗄️ Relational Database Schema Overview

```
airline (airline_id, name, iata_code, country)
airport (airport_id, iata_code, name, city, country)
aircraft_model (model_id, manufacturer, model_name, range_km)
aircraft (aircraft_id, airline_id, model_id, registration_number, manufacture_year, status)
seat (seat_id, aircraft_id, seat_number, seat_class)
flight (flight_id, flight_number, aircraft_id, departure_airport_id, arrival_airport_id, departure_time, arrival_time, base_price, status)
passenger (passenger_id, first_name, last_name, email, password, phone, passport_number, date_of_birth, gender)
booking (booking_id, passenger_id, booking_reference, booking_date, status, total_amount)
payment (payment_id, booking_id, amount, payment_date, payment_method, transaction_reference, status)
ticket (ticket_id, booking_id, flight_id, passenger_id, seat_id, ticket_number, fare, status)
baggage (baggage_id, ticket_id, weight_kg, baggage_type)
employee (employee_id, airline_id, first_name, last_name, email, role, department, hire_date, status)
```

---

## 🛠️ Technology Stack

| Layer | Technologies |
| :--- | :--- |
| **Mobile Frontend** | Android SDK (Java), Material Design 3, ViewBinding, Retrofit 2, OkHttp 3, Gson, LiveData, ViewModel |
| **Backend API** | Java 17/24, Spring Boot 3.2.2, Spring Data JPA, Spring Security, Hibernate ORM, Maven |
| **Database** | MySQL 8.0+ (Production), H2 Database (Dev/Test), SQL Views, Composite B-Tree Indexes |
| **Tooling & CI** | Android Studio, IntelliJ IDEA, Git, cURL, JUnit 5, Mockito |

---

## 📜 Team Git Workflow & Guidelines

Our team practiced strict Git collaboration standards:
- **Feature Branching:** All new work was developed in isolated branches (`feat/backend-hardik`, `frontend-Devansh`, `db_chirag`).
- **Conventional Commits:** Messages strictly categorized (`feat`, `fix`, `docs`, `refactor`, `test`).
- **Clean Rebase & Merge:** Regular rebase onto `main` to maintain linear, conflict-free commit history.
- Detailed developer operating procedures are documented in [`devlog.md`](file:///Users/hardik/Airlines/devlog.md).
