# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Preserve line numbers for readable crash stack traces, hide original file names.
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- kotlinx.serialization ---
# Keep generated serializers and @Serializable members so R8 doesn't strip them.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class **$$serializer { *; }
-keepclasseswithmembers, allowshrinking class * {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep, includedescriptorclasses class com.sitharaj.notes.**$$serializer { *; }
-keepclassmembers class com.sitharaj.notes.** {
    *** Companion;
}
-keepclasseswithmembers class com.sitharaj.notes.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# --- Retrofit / OkHttp ---
# Retrofit & OkHttp ship consumer rules; these cover reflective generic signatures.
-keepattributes Signature, Exceptions
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Keep the data/domain model and DTOs referenced reflectively by serialization.
-keep class com.sitharaj.notes.**.model.** { *; }
-keep class com.sitharaj.notes.data.remote.** { *; }
-keep class com.sitharaj.notes.core.network.** { *; }

# --- Tink (androidx.security.crypto) ---
# Tink references compile-only errorprone annotations not present at runtime.
-dontwarn com.google.errorprone.annotations.**
-dontwarn javax.annotation.**
-keep class com.google.crypto.tink.** { *; }