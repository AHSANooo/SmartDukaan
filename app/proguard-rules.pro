# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep line numbers for better crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep data classes used for JSON serialization
-keep class com.example.smartdukaan.Item { *; }
-keep class com.example.smartdukaan.Sale { *; }
-keep class com.example.smartdukaan.SaleItem { *; }
-keep class com.example.smartdukaan.utils.BackupManager$BackupData { *; }

# Gson rules
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Keep generic signature of Gson TypeToken
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Prevent obfuscation of generic types
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Keep fragment constructors
-keepclassmembers class * extends androidx.fragment.app.Fragment {
    public <init>(...);
}

# Keep activity and fragment names for navigation
-keep class com.example.smartdukaan.*Activity { *; }
-keep class com.example.smartdukaan.*Fragment { *; }
-keep class com.example.smartdukaan.ui.** { *; }

# Keep utility classes
-keep class com.example.smartdukaan.utils.** { *; }

# Keep DataManager singleton
-keep class com.example.smartdukaan.DataManager { *; }

# Remove all logging in release builds
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
}

# Keep annotations
-keepattributes *Annotation*

# AndroidX
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-dontwarn androidx.**

# Material Design
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**
