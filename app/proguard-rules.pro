-keep class com.tradinganalytics.data.database.entities.** { *; }
-keep class com.tradinganalytics.domain.model.** { *; }
-keep class com.tradinganalytics.patterns.library.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
