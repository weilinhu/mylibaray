# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/weilinhu-MAC/Downloads/sdk/android_sdk/tools/proguard/proguard-android.txt
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
#-keep   class com.zhanle.upgrade.Error {*;}
#-keep   class com.zhanle.upgrade.UpgradeListener {*;}
#
#-keep class com.zhanle.upgrade.* {
#    public <methods>;
#}

-keep class com.zhanshow.download.** {
 *;
}

-keep class com.j256.ormlite.** {
 *;
}

-dontwarn org.slf4j.**
-dontwarn org.apache.log4j.**
-dontwarn org.apache.commons.logging.**
-dontwarn org.apache.commons.codec.binary.**
-dontwarn javax.persistence.**
-dontwarn javax.lang.**
-dontwarn javax.annotation.**
-dontwarn javax.tools.**