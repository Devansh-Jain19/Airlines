package com.airline.app;

import android.app.Application;
import com.airline.app.util.SessionManager;

public class AirlineApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize and apply user's saved dark/light theme preference on startup
        SessionManager sessionManager = SessionManager.getInstance(this);
        sessionManager.applyTheme();
    }
}
