# Android Frontend Architecture & Integration Guide (`frontend.md`)

## 1. Frontend Architectural Overview

The Android application is constructed using **Google's recommended MVVM (Model-View-ViewModel)** architectural pattern with clean separation of concerns.

```
┌────────────────────────────────────────────────────────────────────────┐
│                              VIEW LAYER                                │
│       Activities / Fragments / Custom Layouts (ViewBinding)            │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ User Interactions / Clicks
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                           VIEWMODEL LAYER                              │
│          LiveData / StateFlow Holder (Rotational Survival)             │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Calls Repository
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                           REPOSITORY LAYER                             │
│        Central Data Manager (Remote API + Local Session Storage)       │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │ Retrofit Calls (HTTP / JSON)
                                    ▼
┌────────────────────────────────────────────────────────────────────────┐
│                            NETWORK LAYER                               │
│           Retrofit ApiService + OkHttp Logging Interceptor             │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Directory & Package Layout

```
com.airline.app/
├── ui/
│   ├── auth/           # LoginActivity, RegisterActivity
│   ├── search/         # SearchFragment, AirportAdapter
│   ├── flights/        # FlightListActivity, FlightAdapter
│   ├── seats/          # SeatSelectionActivity, SeatGridAdapter
│   ├── booking/        # CheckoutActivity, PaymentBottomSheet
│   └── ticket/         # BoardingPassActivity, MyBookingsActivity
├── viewmodel/          # AuthViewModel, FlightViewModel, BookingViewModel
├── repository/        # AuthRepository, FlightRepository, BookingRepository
├── network/
│   ├── ApiService.java            # Retrofit Endpoint Interfaces
│   ├── RetrofitClient.java        # Singleton Retrofit Instance
│   └── Resource.java              # Generic Network State Wrapper (Success/Error/Loading)
├── model/
│   ├── request/        # LoginRequest, RegisterRequest, BookingRequest
│   └── response/       # FlightSummaryDto, SeatDto, BookingResponseDto
└── util/
    ├── SessionManager.java        # SharedPreferences Session Holder
    └── DateUtils.java             # ISO-8601 Date Formatting Utilities
```

---

## 3. Network Data Contracts & Retrofit Interface

### 3.1 Network Resource Wrapper (`Resource<T>`)
```java
public class Resource<T> {
    public enum Status { SUCCESS, ERROR, LOADING }
    public final Status status;
    public final T data;
    public final String message;

    private Resource(Status status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> success(T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(String msg) {
        return new Resource<>(Status.ERROR, null, msg);
    }

    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null);
    }
}
```

### 3.2 Retrofit Interface (`ApiService.java`)
```java
public interface ApiService {
    @POST("api/auth/register")
    Call<PassengerDto> registerPassenger(@Body RegisterRequest request);

    @POST("api/auth/login")
    Call<AuthResponseDto> loginPassenger(@Body LoginRequest request);

    @GET("api/airports")
    Call<List<AirportDto>> getAirports();

    @GET("api/flights/search")
    Call<List<FlightSummaryDto>> searchFlights(
        @Query("from") String departureIata,
        @Query("to") String arrivalIata,
        @Query("date") String flightDate
    );

    @GET("api/flights/{id}/seats")
    Call<List<SeatDto>> getFlightSeats(@Path("id") Long flightId);

    @POST("api/bookings")
    Call<BookingResponseDto> createBooking(@Body BookingRequest request);

    @POST("api/payments")
    Call<PaymentResponseDto> processPayment(@Body PaymentRequest request);

    @GET("api/tickets/{id}")
    Call<TicketDetailDto> getTicketDetails(@Path("id") Long ticketId);

    @GET("api/passengers/{id}/bookings")
    Call<List<BookingResponseDto>> getPassengerBookings(@Path("id") Long passengerId);

    @DELETE("api/bookings/{id}")
    Call<Void> cancelBooking(@Path("id") Long bookingId);
}
```

---

## 4. ViewModel & UI State Management Example

### `FlightViewModel.java`
```java
public class FlightViewModel extends ViewModel {
    private final FlightRepository repository;
    private final MutableLiveData<Resource<List<FlightSummaryDto>>> flightSearchResults = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<SeatDto>>> seatMapResults = new MutableLiveData<>();

    public FlightViewModel() {
        this.repository = new FlightRepository();
    }

    public LiveData<Resource<List<FlightSummaryDto>>> getFlightSearchResults() {
        return flightSearchResults;
    }

    public LiveData<Resource<List<SeatDto>>> getSeatMapResults() {
        return seatMapResults;
    }

    public void searchFlights(String from, String to, String date) {
        flightSearchResults.setValue(Resource.loading());
        repository.searchFlights(from, to, date, new RepositoryCallback<List<FlightSummaryDto>>() {
            @Override
            public void onSuccess(List<FlightSummaryDto> data) {
                flightSearchResults.postValue(Resource.success(data));
            }

            @Override
            public void onError(String errorMessage) {
                flightSearchResults.postValue(Resource.error(errorMessage));
            }
        });
    }

    public void loadSeatMap(Long flightId) {
        seatMapResults.setValue(Resource.loading());
        repository.loadSeats(flightId, new RepositoryCallback<List<SeatDto>>() {
            @Override
            public void onSuccess(List<SeatDto> seats) {
                seatMapResults.postValue(Resource.success(seats));
            }

            @Override
            public void onError(String error) {
                seatMapResults.postValue(Resource.error(error));
            }
        });
    }
}
```

---

## 5. UI Screen Breakdown & Components

### 5.1 Flight Search Screen (`SearchFragment`)
* **Components**: Two Searchable AutoCompleteTextViews (Origin & Destination IATA), DatePicker Button, Search Button.
* **Validation**: Origin and Destination cannot be identical (`departureIata != arrivalIata`). Date cannot be in the past.

### 5.2 Seat Selection Map (`SeatSelectionActivity`)
* **Components**: Aircraft Aisle Layout (RecyclerView Grid with 4 columns: `A, B | C, D`).
* **Interactive State**:
  * **White Box**: Available Seat (`available == true`)
  * **Grey Box**: Occupied Seat (`available == false`, non-clickable)
  * **Blue Accent Box**: Currently Selected Seat by User
* **Business Rule Enforcement**: User can select up to the number of passenger tickets in the current segment.

### 5.3 Checkout & Payment Screen (`CheckoutActivity`)
* **Components**: Summary Card (Flight Number, Time, Seat Numbers, Calculated Total Fare), Radio Group for Payment Method (`CARD`, `UPI`, `NET_BANKING`, `WALLET`), Pay Button.
* **Flow**:
  1. Invokes `POST /api/bookings` -> returns status `PENDING`.
  2. Invokes `POST /api/payments` -> returns status `SUCCESS`, updates booking status to `CONFIRMED`.
  3. Navigates to `BoardingPassActivity`.

---

## 6. Client Networking Setup & Base URL

In `RetrofitClient.java`:
```java
public class RetrofitClient {
    // 10.0.2.2 points to host machine localhost inside Android Emulator
    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static Retrofit retrofit = null;

    public static ApiService getApiService() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();

            retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }
        return retrofit.create(ApiService.class);
    }
}
```
