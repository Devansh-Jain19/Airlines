# ProGuard rules for Airline App

# Keep Retrofit and Gson classes
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.airline.app.model.** { *; }
-keep class com.airline.app.network.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
