# Optimizations
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-dontwarn org.jaudiotagger.**
-dontwarn java.lang.invoke.*
-dontwarn **$$Lambda$*
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v7.app.** { *; }
-keep interface android.support.v7.app.** { *; }
-keep class android.support.** { *; }
-keep interface android.support.app.** { *; }
-keep class org.jaudiotagger.** { *;}
-keep interface org.jaudiotagger.** { *;}
-keepattributes Exceptions
-keepattributes Signature
-keepattributes *Annotation*
-keep class javax.** { *; }
-keep class org.** { *; }
-keep class com.afollestad.appthemeengine.** { *; }
-dontwarn android.support.v8.renderscript.*
-keepclassmembers class android.support.v8.renderscript.RenderScript {
  native *** rsn*(...);
  native *** n*(...);
}
-keep class android.support.v7.widget.SearchView { *; }
-dontwarn java.lang.invoke.*
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
-dontwarn rx.**
-dontwarn javax.annotation.**
-keep class com.squareup.okhttp.** { *; }
-keep class retrofit.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.cleveroad.audiowidget.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepattributes SourceFile,LineNumberTable
-keepclasseswithmembernames class * {
    native <methods>;
}  
-keep class android.support.v8.renderscript.** { *; }
-dontwarn jp.co.cyberagent.android.gpuimage.**