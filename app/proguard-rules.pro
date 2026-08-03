# Add project specific ProGuard rules here.

# Retrofit / OkHttp - keep generic signatures and annotations used at runtime via reflection
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-keepattributes Exceptions

# Moshi - keep generated JsonAdapters and the data classes they serialize
-keep class kotlin.Metadata { *; }
-keepclassmembers @com.squareup.moshi.JsonClass class * {
    <init>(...);
}
-keep @com.squareup.moshi.JsonClass class * { *; }
-dontwarn com.squareup.moshi.**

# Hilt / Dagger
-dontwarn com.google.errorprone.annotations.**

# Our @NoAuth annotation must survive reflection lookups in AuthInterceptor
-keep @interface com.example.swiftshare.core.network.NoAuth
-keepattributes *Annotation*

# Room
-keep class * extends androidx.room.RoomDatabase
