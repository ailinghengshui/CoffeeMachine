# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\sdk/tools/proguard/proguard-android.txt
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

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify
-verbose
-printmapping proguardMapping.txt
-optimizations !code/simplification/cast,!field/*,!class/merging/*
-keepattributes *Annotation*,InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService
-keep class android.support.** {*;}
-keep public class * extends android.database.sqlite.SQLiteOpenHelper{*;}

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class **.R$* {
 *;
}
-keepclassmembers class * {
    void *(**On*Event);
}

-keepclasseswithmembers class * implements com.jingye.coffeemac.common.action.TAction {
}

-keepclasseswithmembers class * implements com.jingye.coffeemac.common.action.IAction {
}

-keepclasseswithmembers class * implements com.jingye.coffeemac.service.action.TAction {
}

-keepclasseswithmembers class * implements com.jingye.coffeemac.service.action.IAction {
}

-keepclasseswithmembers class * implements com.jingye.coffeemac.common.database.IDataSet {
}

-keepclasseswithmembers class * extends com.jingye.coffeemac.common.database.SqlMaper {
}

-keepclasseswithmembers class * extends com.jingye.coffeemac.service.domain.Ancestor {
}

-keepclasseswithmembers class * extends com.jingye.coffeemac.common.adapter.TViewHolder {
}


-keepclasseswithmembers class * extends com.jingye.coffeemac.common.adapter.TListItem {
}
-keepclasseswithmembers class * extends android.widget.BaseAdapter{
}

-keep class com.jingye.coffeemac.common.database.**{*;}

-keep class com.jingye.coffeemac.common.dbhelper.**{*;}
-keep class com.jingye.coffeemac.common.adapter.**{*;}



-keep class com.jingye.coffeemac.beans.**{*;}
-keep class com.jingye.coffeemac.domain.**{*;}
-keep class com.jingye.coffeemac.adapter.**{*;}
-keep class com.jingye.coffeemac.service.protocol.**{*;}
-keep class com.jingye.coffeemac.service.protocol.enums.**{*;}
-keep class com.jingye.coffeemac.service.domain.**{*;}


#fastjson
-keepattributes Signature
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.**{*; }

#netty
 # Get rid of warnings about unreachable but unused classes referred to by Netty
-dontwarn org.jboss.netty.**

# Needed by commons logging
-keep class org.apache.commons.logging.* {*;}

#Some Factory that seemed to be pruned
-keep class java.util.concurrent.atomic.AtomicReferenceFieldUpdater {*;}
-keep class java.util.concurrent.atomic.AtomicReferenceFieldUpdaterImpl{*;}

#Some important internal fields that where removed
-keep class org.jboss.netty.channel.DefaultChannelPipeline{volatile <fields>;}

#A Factory which has a static factory implementation selector which is pruned
-keep class org.jboss.netty.util.internal.QueueFactory{static <fields>;}

#Some fields whose names need to be maintained because they are accessed using inflection
-keepclassmembernames class org.jboss.netty.util.internal.**{*;}



#zxing
-dontwarn com.google.zxing.**
-keep  class com.google.zxing.**{*;}

#async
-keep class cz.msebera.android.httpclient.** { *; }
-keep class com.loopj.android.http.** { *; }


#glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-dontwarn com.bumptech.glide.**
-keep class com.bumptech.glide.** { *;}


-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment

#百度地图混淆
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**

#qiniu
-dontwarn com.qiniu.**
-keep class com.qiniu.**{*;}
-keep class com.qiniu.**{public <init>();}
-ignorewarnings