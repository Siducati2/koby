# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Kevin\AppData\Local\Android\android-studio\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}


-dontwarn com.squareup.okhttp.**
-dontwarn android.support.v4.**
-keep public class com.google.android.gms.* { public *; }
-dontwarn com.google.android.gms.**
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}
-dontwarn butterknife.Views$InjectViewProcessor
-dontwarn com.gc.materialdesign.views.**
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}

-keepclassmembers class * implements android.os.Parcelable {
    static ** CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

-keepattributes Exceptions,InnerClasses,Signature

-keep class com.google.api.client.**
-keepclassmembers class com.google.api.client.** {
    *;
 }


-keep class com.google.android.gms.**
-keepclassmembers class com.google.android.gms.** {
    *;
 }
-keep class com.google.gson.**
-keepclassmembers class com.google.gson.** {
    *;
 }


-keep class com.google.ads.**
-keepclassmembers class com.google.ads.** {
    *;
 }

-keep class bolts.**
-keepclassmembers class bolts.** {
    *;
 }

-keep class com.google.api.client.** { *; }
-dontwarn com.google.api.client.*
-keep class org.apache.http.** { *; }
-dontwarn org.apache.http.*

 # The official support library.
-keep class android.support.v4.** { *; }
-keepclassmembers class android.support.v4.** {
    *;
 }
-keep interface android.support.v4.** { *; }

-keep class android.support.v7.internal.** { *; }
-keep interface android.support.v7.internal.** { *; }
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

-keep class android.support.v7.** { *; }
-keepclassmembers class android.support.v7.** {
    *;
 }
-dontwarn android.support.v7.**
-keep interface android.support.v7.** { *; }

# Library JARs.
-keep class com.facebook.** {
   *;
}
-keepclassmembers class com.facebook.** {
    *;
 }