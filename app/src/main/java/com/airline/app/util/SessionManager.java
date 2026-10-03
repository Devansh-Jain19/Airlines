package com.airline.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import com.airline.app.model.response.AuthResponseDto;
import com.airline.app.model.response.PassengerDto;

public class SessionManager {
    private static final String PREF_NAME = "airline_app_session";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_TOKEN = "jwt_token";
    private static final String KEY_PASSENGER_ID = "passenger_id";
    private static final String KEY_EMAIL = "passenger_email";
    private static final String KEY_NAME = "passenger_name";
    private static final String KEY_PHONE = "passenger_phone";
    private static final String KEY_PASSPORT = "passenger_passport";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private static SessionManager instance;

    public SessionManager(Context context) {
        pref = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public void saveAuthSession(AuthResponseDto authResponse) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        if (authResponse.getToken() != null) {
            editor.putString(KEY_TOKEN, authResponse.getToken());
        }
        if (authResponse.getPassengerId() != null) {
            editor.putLong(KEY_PASSENGER_ID, authResponse.getPassengerId());
        }
        if (authResponse.getEmail() != null) {
            editor.putString(KEY_EMAIL, authResponse.getEmail());
        }
        if (authResponse.getName() != null) {
            editor.putString(KEY_NAME, authResponse.getName());
        }
        if (authResponse.getPassenger() != null) {
            PassengerDto p = authResponse.getPassenger();
            if (p.getId() != null) editor.putLong(KEY_PASSENGER_ID, p.getId());
            if (p.getEmail() != null) editor.putString(KEY_EMAIL, p.getEmail());
            if (p.getFullName() != null && !p.getFullName().trim().isEmpty()) {
                editor.putString(KEY_NAME, p.getFullName().trim());
            }
            if (p.getPhone() != null) editor.putString(KEY_PHONE, p.getPhone());
            if (p.getPassportNumber() != null) editor.putString(KEY_PASSPORT, p.getPassportNumber());
        }
        editor.apply();
    }

    public void savePassenger(PassengerDto passenger) {
        if (passenger != null) {
            if (passenger.getId() != null) editor.putLong(KEY_PASSENGER_ID, passenger.getId());
            if (passenger.getEmail() != null) editor.putString(KEY_EMAIL, passenger.getEmail());
            if (passenger.getFullName() != null) editor.putString(KEY_NAME, passenger.getFullName().trim());
            if (passenger.getPhone() != null) editor.putString(KEY_PHONE, passenger.getPhone());
            if (passenger.getPassportNumber() != null) editor.putString(KEY_PASSPORT, passenger.getPassportNumber());
            editor.apply();
        }
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getToken() {
        return pref.getString(KEY_TOKEN, "");
    }

    public long getPassengerId() {
        return pref.getLong(KEY_PASSENGER_ID, -1L);
    }

    public String getEmail() {
        return pref.getString(KEY_EMAIL, "");
    }

    public String getName() {
        return pref.getString(KEY_NAME, "Traveler");
    }

    public String getPhone() {
        return pref.getString(KEY_PHONE, "");
    }

    public String getPassportNumber() {
        return pref.getString(KEY_PASSPORT, "");
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
