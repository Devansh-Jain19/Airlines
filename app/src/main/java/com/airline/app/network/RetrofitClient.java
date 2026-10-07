package com.airline.app.network;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    // Dynamic Base URL: 10.0.2.2 for Android Studio Emulator, 127.0.0.1 (adb reverse) for real devices
    private static String BASE_URL = null;
    private static Retrofit retrofit = null;
    private static ApiService apiService = null;

    private static boolean isEmulator() {
        return (android.os.Build.BRAND.startsWith("generic") && android.os.Build.DEVICE.startsWith("generic"))
                || android.os.Build.FINGERPRINT.startsWith("generic")
                || android.os.Build.FINGERPRINT.startsWith("unknown")
                || android.os.Build.HARDWARE.contains("goldfish")
                || android.os.Build.HARDWARE.contains("ranchu")
                || android.os.Build.MODEL.contains("google_sdk")
                || android.os.Build.MODEL.contains("Emulator")
                || android.os.Build.MODEL.contains("Android SDK built for x86")
                || android.os.Build.MANUFACTURER.contains("Genymotion")
                || android.os.Build.PRODUCT.contains("sdk_google")
                || android.os.Build.PRODUCT.contains("google_sdk")
                || android.os.Build.PRODUCT.contains("sdk")
                || android.os.Build.PRODUCT.contains("sdk_x86")
                || android.os.Build.PRODUCT.contains("vbox86p")
                || android.os.Build.PRODUCT.contains("emulator")
                || android.os.Build.PRODUCT.contains("simulator");
    }

    public static synchronized ApiService getApiService() {
        if (retrofit == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

            retrofit = new Retrofit.Builder()
                .baseUrl(getBaseUrl())
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

            apiService = retrofit.create(ApiService.class);
        }
        return apiService;
    }

    public static synchronized void setBaseUrl(String newBaseUrl) {
        if (newBaseUrl != null && !newBaseUrl.trim().isEmpty()) {
            if (!newBaseUrl.endsWith("/")) {
                newBaseUrl = newBaseUrl + "/";
            }
            BASE_URL = newBaseUrl;
            retrofit = null;
            apiService = null;
        }
    }

    public static String getBaseUrl() {
        if (BASE_URL == null) {
            BASE_URL = isEmulator() ? "http://10.0.2.2:8081/" : "http://127.0.0.1:8081/";
        }
        return BASE_URL;
    }
}
