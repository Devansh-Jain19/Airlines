# Complete Beginner's Setup Guide: Airlines Flight Reservation System (`SETUP.md`)

Welcome! If you are new to this project or setting up a full-stack system for the first time, this guide is designed for you. It explains step-by-step how to set up the **Database**, **Backend (Spring Boot)**, and **Frontend (Android)** with zero confusion.

---

## Table of Contents
1. [Architecture & Port Map](#1-architecture--port-map)
2. [Prerequisites & Tool Installation](#2-prerequisites--tool-installation)
3. [Step 1: Database Setup (MySQL & In-Memory H2)](#3-step-1-database-setup)
4. [Step 2: Backend Setup (Spring Boot 3.x)](#4-step-2-backend-setup)
5. [Step 3: Frontend Setup (Web App on Localhost & Android App)](#5-step-3-frontend-setup)
6. [Step 4: End-to-End Testing Walkthrough](#6-step-4-end-to-end-testing-walkthrough)
7. [Common Pitfalls & Troubleshooting FAQ](#7-common-pitfalls--troubleshooting-faq)

---

## 1. Architecture & Port Map

Our project consists of frontend clients connecting to a robust backend & database layer:

```
┌──────────────────────────────────────┐     ┌──────────────────────────────────────┐
│      1A. Web Frontend (Localhost)    │     │      1B. Native Android Mobile App   │
│      Port: 3000 (Desktop Browser)    │     │       (Emulator or Physical Phone)   │
└──────────────────┬───────────────────┘     └──────────────────┬───────────────────┘
                   │                                            │
                   └───────────────────┬────────────────────────┘
                                       │ HTTP REST Calls (JSON)
                                       ▼
┌───────────────────────────────────────────────────────────────────────────────────┐
│                            2. Spring Boot 3.x Backend                             │
│                              Port: 8081 (Tomcat Server)                           │
└──────────────────────────────────────┬────────────────────────────────────────────┘
                                       │ JDBC / Hibernate (JPA)
                                       ▼
┌───────────────────────────────────────────────────────────────────────────────────┐
│                               3. Relational Database                              │
│                   Option A: In-Memory H2 (Default, Zero-Config)                   │
│                   Option B: MySQL 8.0+ on Port 3306 (Production)                  │
└───────────────────────────────────────────────────────────────────────────────────┘
```

### Network & Port Reference Table

| Component | Default Host / IP | Port | Notes |
| :--- | :--- | :--- | :--- |
| **Web Frontend** | `localhost` | `3000` | Responsive web UI connecting directly to Spring Boot REST API |
| **Backend REST API** | `localhost` (Host machine) | `8081` | Changed from 8080 to prevent conflicts with local web servers |
| **MySQL Database** | `localhost` | `3306` | Default MySQL port |
| **Android Emulator Bridge** | `10.0.2.2` | `8081` | `10.0.2.2` is the special alias emulator uses to reach host machine `localhost` |
| **Physical Android Device** | `<Your_Computer_LAN_IP>` | `8081` | Example: `192.168.1.15:8081` (Phone & PC must be on same Wi-Fi) |

---

## 2. Prerequisites & Tool Installation

Before running the project, install the following required developer tools:

### A. Java Development Kit (JDK 17 or higher)
Spring Boot 3 requires Java 17 minimum (Java 17, 21, or 24 are fully supported).

- **Verify installation:**
  ```bash
  java -version
  ```
  *Expected Output:* `openjdk version "17.x.x"` (or higher).
- **If not installed:**
  - **macOS (via Homebrew):**
    ```bash
    brew install openjdk@17
    ```
  - **Windows:** Download and install Microsoft Build of OpenJDK 17 or Eclipse Temurin 17 from [Adoptium](https://adoptium.net/).
  - **Linux (Ubuntu/Debian):**
    ```bash
    sudo apt update && sudo apt install -y openjdk-17-jdk
    ```

### B. MySQL Community Server 8.0+ (Optional for quickstart, required for standalone DB testing)
- **Verify installation:**
  ```bash
  mysql --version
  ```
- **If not installed:**
  - **macOS:** `brew install mysql && brew services start mysql`
  - **Windows:** Download MySQL Community Server via MySQL Installer from [dev.mysql.com](https://dev.mysql.com/downloads/installer/).
  - **Linux:** `sudo apt install -y mysql-server && sudo systemctl start mysql`

### C. Android Studio (Hedgehog, Iguana, Ladybug, or latest)
- Download Android Studio from [developer.android.com/studio](https://developer.android.com/studio).
- During installation, ensure the following components are checked:
  - **Android SDK**
  - **Android SDK Platform-Tools**
  - **Android Virtual Device (AVD)**

### D. Git & cURL
- Ensure Git is installed:
  ```bash
  git --version
  ```

---

## 3. Step 1: Database Setup

You have **two options** to run the database. Choose **Option A** if you want to get up and running immediately in under 1 minute. Choose **Option B** if you want to inspect MySQL directly using SQL clients.

### Option A: Zero-Configuration Mode (Built-in H2 MySQL Mode - Recommended for Beginners)
The backend is already pre-configured to automatically initialize an in-memory database with full MySQL syntax compatibility!
- **How it works:** When you start the backend, Spring Boot's [`DataInitializer.java`](file:///Users/hardik/Airlines/backend/src/main/java/com/airline/config/DataInitializer.java) runs automatically and populates:
  - 10 international and domestic airports (DEL, BOM, BLR, HYD, MAA, CCU, JFK, LAX, LHR, DXB)
  - 4 major airlines (IndiGo, Air India, Delta, British Airways)
  - Aircraft & 4-column seat grids (Business and Economy class)
  - Real scheduled flights between major cities
  - Demo user accounts (`aarav@gmail.com` and `traveler@airline.com` with password `Password@123`)
- **Action required:** *None!* Proceed directly to [Step 2](#4-step-2-backend-setup).

---

### Option B: Full MySQL 8.0 Setup (Standalone Relational Database)
If you want to run the enterprise MySQL database with full constraints, indexes, and views:

1. **Start the MySQL Server:**
   - **macOS:** `brew services start mysql`
   - **Windows:** Start MySQL service from Services (`services.msc`) or MySQL Notifier.
   - **Linux:** `sudo systemctl start mysql`

2. **Log in to MySQL:**
   ```bash
   mysql -u root -p
   ```
   *(Enter your MySQL root password).*

3. **Create the Database:**
   ```sql
   CREATE DATABASE IF NOT EXISTS airline_db;
   USE airline_db;
   ```

4. **Execute SQL Scripts in Strict Order:**
   Run the scripts located in the root repository folder:
   ```bash
   # From the project root folder:
   mysql -u root -p airline_db < schema.sql
   mysql -u root -p airline_db < indexes.sql
   mysql -u root -p airline_db < views.sql
   mysql -u root -p airline_db < seed.sql
   ```
   *Script Execution Order Rationale:*
   - `schema.sql`: Sets up all 12 tables and foreign key relationships.
   - `indexes.sql`: Creates performance indexes on frequently queried search columns.
   - `views.sql`: Compiles SQL analytical views (`v_booking_total`, `v_flight_occupancy`, etc.).
   - `seed.sql`: Populates over 80+ realistic sample records per core table.

5. **Verify Database Content:**
   ```sql
   mysql -u root -p -e "USE airline_db; SELECT COUNT(*) FROM flight; SELECT COUNT(*) FROM airport;"
   ```

6. **Configure Backend to use MySQL:**
   Open [`backend/src/main/resources/application.properties`](file:///Users/hardik/Airlines/backend/src/main/resources/application.properties) and update the datasource lines:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/airline_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
   ```

---

## 4. Step 2: Backend Setup

The backend is built with **Spring Boot 3.2.2** and **Maven Wrapper** (no separate Maven install required).

### 1. Open Terminal and Navigate to Backend Folder
```bash
cd backend
```

### 2. Verify Port & Configurations
The backend server runs on port **8081** (defined in `application.properties`):
```properties
server.port=8081
```
> **Why Port 8081?** Port 8080 is often occupied by other web servers, proxy tools, or Docker. Port 8081 ensures clean startup without port collisions.

### 3. Ensure Maven Wrapper is Executable (macOS/Linux only)
```bash
chmod +x mvnw
```

### 4. Build and Run the Backend Server
- **On macOS / Linux:**
  ```bash
  ./mvnw spring-boot:run
  ```
- **On Windows (Command Prompt / PowerShell):**
  ```cmd
  mvnw.cmd spring-boot:run
  ```

### 5. Check Console Output
You will see Spring Boot start up:
```text
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.2)

...
Tomcat started on port 8081 (http) with context path ''
Started AirlineApplication in 3.456 seconds
```

### 6. Verify Backend Endpoints
Open your web browser or run this cURL command in a separate terminal:
```bash
curl http://localhost:8081/api/airports
```
*Expected Response:* A JSON array containing DEL, BOM, BLR, JFK, LAX, etc.

### 7. Run Automated Backend Tests
To verify all controllers, repositories, and business services:
```bash
./mvnw test
```
All tests should pass cleanly!

---

## 5. Step 3: Frontend Setup

You can interact with the Airlines Reservation System using either the **Web Frontend on Localhost** (easiest & instant) or the **Native Android Mobile App**.

---

### Option A: Web Frontend on Localhost (Recommended — Quick & Instant)

The web frontend (**AeroSkyline**) is located in the [`web-frontend/`](file:///d:/Airlines/Airlines/web-frontend) folder and connects directly to the Spring Boot REST API on `http://localhost:8081`.

#### 1. Start the Web Frontend Server
Run any of the following commands from the repository root:

- **Method 1 (1-Click Batch Script on Windows):**
  ```cmd
  .\run_web_frontend.bat
  ```

- **Method 2 (Using Node.js / NPX):**
  ```powershell
  cd web-frontend
  npx serve -p 3000 .
  ```

- **Method 3 (Using Python):**
  ```powershell
  cd web-frontend
  python -m http.server 3000
  ```

#### 2. Open in Your Browser
Navigate to:
```
http://localhost:3000
```

#### 3. Features Included:
- **Live Status Monitoring:** Status pill in navbar confirms connection to Spring Boot backend.
- **Flight Discovery:** Dynamic origin/destination airports populated from `/api/airports`, date picker, cabin class filters, and popular routes (`DEL ➔ BOM`, `BOM ➔ BLR`, `DEL ➔ DXB`).
- **Interactive 2D Aircraft Cabin:** Real-time seat map loaded from `/api/flights/{id}/seats` with business & economy rows, seat selection, and price calculation.
- **Fare & Baggage Breakdown:** Dynamic baggage options (+5kg, +10kg) and 12% aviation taxes.
- **Simulated Checkout & Payment:** Card, UPI, and Net Banking checkout triggering `/api/bookings` and `/api/payments`.
- **Luxury Boarding Pass:** Generates a full digital ticket with PNR, barcode, and print/PDF options.
- **Account Management:** Sign In with demo credentials (`aarav@gmail.com` / `Password@123`), registration, and **My Bookings** with 1-click booking cancellation.

---

### Option B: Native Android Mobile App (Android Studio)

The mobile client is a native Android application built with Java, Material Design 3, and Retrofit 2 located in the [`app/`](file:///d:/Airlines/Airlines/app) module.

#### 1. Open Android Studio
1. Launch **Android Studio**.
2. Click **File** -> **Open...**
3. Browse to and select the project directory: `d:\Airlines\Airlines`.
4. Click **OK** to open the project.

#### 2. Allow Gradle to Sync
- Android Studio will automatically start downloading required dependencies and syncing Gradle.
- Wait until the progress bar at the bottom right says **"Gradle sync finished"**.

#### 3. Verify Server URL Configuration in `RetrofitClient.java`
Open the file [`app/src/main/java/com/airline/app/network/RetrofitClient.java`](file:///d:/Airlines/Airlines/app/src/main/java/com/airline/app/network/RetrofitClient.java):
```java
// Default setting for Android Emulator:
private static String BASE_URL = "http://10.0.2.2:8081/";
```

##### Understanding the Base URL:
- **Scenario A: Running in Android Emulator (Default):**
  - Leave `http://10.0.2.2:8081/` as-is.
  - The Android emulator runs in its own virtual network. The IP `10.0.2.2` automatically routes traffic from the emulator directly to your computer's `localhost:8081`.
- **Scenario B: Running on a Physical Android Smartphone via USB / Wi-Fi:**
  1. Connect your phone and computer to the **same Wi-Fi network**.
  2. Find your computer's local IP address:
     - **macOS / Linux:** Open terminal and run `ipconfig getifaddr en0` or `ifconfig` (e.g., `192.168.1.45`).
     - **Windows:** Open command prompt and run `ipconfig` (find IPv4 Address under Wireless LAN adapter, e.g., `192.168.1.45`).
  3. Change `BASE_URL` in `RetrofitClient.java` to:
     ```java
     private static String BASE_URL = "http://192.168.1.45:8081/";
     ```

#### 4. Create or Start an Android Virtual Device (AVD Emulator)
1. In Android Studio, open the **Device Manager** (phone icon in upper right corner or `Tools` -> `Device Manager`).
2. If no device exists, click **Create Device**.
3. Select **Pixel 7** or **Pixel 8**, click **Next**.
4. Select system image: **API 33 (Tiramisu)** or **API 34 (UpsideDownCake)**, click **Download** if needed, then **Next** -> **Finish**.
5. Click the green **Play** button next to your virtual device to boot it up.

#### 5. Launch the Android App
1. Make sure your Spring Boot backend is already running on port 8081.
2. In Android Studio, ensure the run target dropdown at the top displays **app** and your emulator is selected.
3. Click the green **Run 'app'** button (or press `Shift + F10`).
4. The application will compile, install on the emulator, and launch automatically!

---

## 6. Step 4: End-to-End Testing Walkthrough

Follow these steps on the running Android app to test the entire system end-to-end:

### Step 1: Login or Register
- **Option 1 (Quick Login):**
  - Email: `aarav@gmail.com`
  - Password: `Password@123`
  - Tap **Log In**.
- **Option 2 (New Registration):**
  - Tap **Create Account** / **Register**.
  - Enter your First Name, Last Name, Email, Password (e.g. `User@123`), Phone, and Passport Number.
  - Tap **Register**. You will receive a success confirmation and be directed to the main search screen.

### Step 2: Search for Flights
- On the **Search** screen:
  - **From:** Tap and select or type `DEL` (Indira Gandhi International Airport).
  - **To:** Tap and select or type `BOM` (Chhatrapati Shivaji Maharaj International Airport).
  - **Departure Date:** Pick tomorrow's date (or any date matching the seeded flights).
  - Tap **Search Flights**.

### Step 3: Browse Flight Results
- View available flights (e.g. `6E201` IndiGo, `6E205`, `AI502` Air India).
- Compare base fares and departure/arrival times.
- Tap any flight card to proceed to seat selection.

### Step 4: Choose Your Seat
- The interactive seat screen displays an aircraft cabin grid (Columns **A - B [Aisle] C - D**).
  - Row 1 & 2: **Business Class**
  - Row 3 to 12: **Economy Class**
- Green/Blue seats are **Available**. Gray seats are **Occupied**.
- Tap an available seat (e.g. `3A`). It turns into a highlighted **Selected** state.
- Tap **Continue to Checkout**.

### Step 5: Checkout & Baggage Selection
- Review the trip summary: Flight number, Origin, Destination, and Seat.
- Select your baggage weight option (e.g., Standard 15kg or Extra 20kg/25kg).
- The total price dynamically updates with taxes and fare breakdown.
- Tap **Proceed to Payment**.

### Step 6: Mock Payment Authorization
- The **Payment Bottom Sheet** slides up from the bottom.
- Select payment method (**Credit/Debit Card**, **UPI**, or **Net Banking**).
- Enter sample card details or tap **Authorize Payment**.
- The backend verifies seat lock, creates the booking, records payment, and issues ticket.

### Step 7: Digital Boarding Pass
- The **Boarding Pass Activity** opens immediately.
- Displays:
  - 6-character Unique Booking Reference (e.g., `BK8F29A`)
  - Passenger Name, Flight Number, Gate, Seat Number, Class
  - Scannable visual barcode representation for airport gate check-in.

### Step 8: View & Cancel in "My Bookings"
- Go back or tap **My Bookings** in the navigation bar.
- See your confirmed reservation in your trip history.
- Tap **Cancel Booking** to test the cancellation lifecycle — the backend marks the ticket as `CANCELLED`, frees up the seat, and updates the booking status!

---

## 7. Common Pitfalls & Troubleshooting FAQ

### 1. Backend error: `Web server failed to start. Port 8081 was already in use.`
- **Cause:** Another instance of the backend or another app is running on port 8081.
- **Fix:**
  - **macOS / Linux:** Run `lsof -i :8081` -> Note the PID -> Run `kill -9 <PID>`.
  - **Windows:** Run `netstat -ano | findstr :8081` -> Run `taskkill /PID <PID> /F`.
  - Or change `server.port=8082` in `application.properties` and update `RetrofitClient.java` accordingly.

### 2. Android App error: `CLEARTEXT communication to 10.0.2.2 not permitted by network security policy`
- **Explanation:** Modern Android versions (Android 9+) block unencrypted HTTP by default.
- **Verification:** Our project already includes `android:usesCleartextTraffic="true"` in [`app/src/main/AndroidManifest.xml`](file:///Users/hardik/Airlines/app/src/main/AndroidManifest.xml). If you encounter this, ensure your manifest contains:
  ```xml
  <application
      android:usesCleartextTraffic="true"
      ... >
  ```

### 3. Android App error: `Failed to connect to /10.0.2.2:8081` or `Connection timed out`
- **Checklist:**
  1. Is the Spring Boot backend currently running? Check your backend terminal for `Started AirlineApplication`.
  2. Test `http://localhost:8081/api/airports` in your browser. If that doesn't load, the backend isn't up.
  3. Are you using a physical phone instead of the emulator? If using a physical phone, `10.0.2.2` will not work. Change `BASE_URL` in `RetrofitClient.java` to your machine's LAN IP (e.g., `http://192.168.1.15:8081/`).

### 4. Maven error: `./mvnw: Permission denied`
- **Fix:** Run `chmod +x mvnw` in the `backend/` folder to grant execution rights.

### 5. MySQL error: `Access denied for user 'root'@'localhost'`
- **Fix:** Ensure the password set in `backend/src/main/resources/application.properties` matches your local MySQL root password. Alternatively, set environment variables:
  ```bash
  export DB_PASSWORD=your_actual_password
  ./mvnw spring-boot:run
  ```

### 6. Android Studio: `Gradle sync failed` or `Unsupported class file major version`
- **Fix:** Ensure Android Studio is using JDK 17+. Go to **Settings / Preferences** -> **Build, Execution, Deployment** -> **Build Tools** -> **Gradle** -> Set **Gradle JDK** to **Embedded JDK (17+)** or **Java 17**.

---

Congratulations! You now have the complete Airline Flight Reservation System running smoothly across your database, backend, and mobile frontend. Enjoy exploring and building!
