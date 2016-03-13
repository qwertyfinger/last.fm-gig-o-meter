-keepclassmembers class ** {
    public void onEvent*(***);
}

-keepclassmembers, includedescriptorclasses class ** {
    public void onEventMainThread*(**);
}

# Only required if you use AsyncExecutor
#-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
#    <init>(java.lang.Throwable);
#}

-dontwarn org.greenrobot.event.util.*$Support
-dontwarn org.greenrobot.event.util.*$SupportManagerFragment