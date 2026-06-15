package com.tradinganalytics.core.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

fun Long.toDateTimeString(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
    return dateTime.format(formatter)
}

fun Long.toDateString(pattern: String = "yyyy-MM-dd"): String {
    val formatter = DateTimeFormatter.ofPattern(pattern)
    val date = LocalDateTime.ofInstant(Instant.ofEpochMilli(this), ZoneId.systemDefault())
    return date.format(formatter)
}

fun Long.toRelativeTime(): String {
    val now = Instant.now()
    val instant = Instant.ofEpochMilli(this)
    val duration = Duration.between(instant, now)

    return when {
        duration.isNegative -> "Just now"
        duration.seconds < 60 -> "Just now"
        duration.toMinutes() < 60 -> "${duration.toMinutes()}m ago"
        duration.toHours() < 24 -> "${duration.toHours()}h ago"
        duration.toDays() < 7 -> "${duration.toDays()}d ago"
        duration.toDays() < 30 -> "${duration.toDays() / 7}w ago"
        duration.toDays() < 365 -> "${duration.toDays() / 30}mo ago"
        else -> "${duration.toDays() / 365}y ago"
    }
}

fun LocalDate.formatDate(pattern: String = "MMM dd, yyyy"): String {
    return this.format(DateTimeFormatter.ofPattern(pattern))
}

fun LocalDateTime.formatDateTime(pattern: String = "MMM dd, yyyy HH:mm"): String {
    return this.format(DateTimeFormatter.ofPattern(pattern))
}

fun Double.formatCurrency(showSymbol: Boolean = true): String {
    val symbol = if (showSymbol) "PKR " else ""
    return try {
        val formatter = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
            isGroupingUsed = true
        }
        "$symbol${formatter.format(this)}"
    } catch (e: Exception) {
        "$symbol${"%.2f".format(this)}"
    }
}

fun Double.formatCompactCurrency(): String {
    return when {
        abs(this) >= 1_000_000_000 -> "${"%.2f".format(this / 1_000_000_000)}B"
        abs(this) >= 1_000_000 -> "${"%.2f".format(this / 1_000_000)}M"
        abs(this) >= 1_000 -> "${"%.2f".format(this / 1_000)}K"
        else -> "%.2f".format(this)
    }
}

fun Double.formatPercentage(signed: Boolean = false): String {
    val prefix = if (signed && this > 0) "+" else ""
    return "$prefix${"%.2f".format(this)}%"
}

fun Double.formatDecimal(decimals: Int = 2): String {
    return "%.${decimals}f".format(this)
}

fun Double.roundTo(decimals: Int): Double {
    return BigDecimal(this).setScale(decimals, RoundingMode.HALF_UP).toDouble()
}

fun Double.formatPrice(maxDecimals: Int = 6): String {
    val decimalPlaces = when {
        this >= 1000 -> 2
        this >= 1 -> 4
        this >= 0.01 -> 6
        else -> 8
    }
    val actualDecimals = decimalPlaces.coerceAtMost(maxDecimals)
    return "%,.${actualDecimals}f".format(this)
}

fun Double.toPips(decimals: Int = 5): Int {
    return (this * Math.pow(10.0, decimals.toDouble())).roundToInt()
}

fun Double.isPositive(): Boolean = this > 0.0

fun Double.isNegative(): Boolean = this < 0.0

fun Double.isZero(tolerance: Double = 0.0001): Boolean = abs(this) < tolerance

fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPhone(): Boolean {
    return this.matches(Regex("^[+]?[0-9]{10,15}$"))
}

fun String.isValidUsername(): Boolean {
    return this.matches(Regex("^[a-zA-Z0-9_]{3,32}$"))
}

fun String.isValidPassword(): Boolean {
    return this.length >= 6 && this.any { it.isUpperCase() } && this.any { it.isDigit() }
}

fun String.capitalizeWords(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString() }
    }
}

fun String.truncate(maxLength: Int, ellipsis: String = "..."): String {
    return if (this.length <= maxLength) this else this.take(maxLength - ellipsis.length) + ellipsis
}

fun String.maskEmail(): String {
    val parts = this.split("@")
    if (parts.size != 2) return this
    val name = parts[0]
    val domain = parts[1]
    return if (name.length <= 2) "${name.first()}***@$domain"
    else "${name.first()}${"*".repeat(name.length - 2)}${name.last()}@$domain"
}

fun String.toDoubleSafe(default: Double = 0.0): Double {
    return try {
        this.replace(",", "").toDouble()
    } catch (e: NumberFormatException) {
        default
    }
}

fun String.toLongSafe(default: Long = 0L): Long {
    return try {
        this.replace(",", "").toLong()
    } catch (e: NumberFormatException) {
        default
    }
}

fun String.extractNumbers(): List<Double> {
    return Regex("-?\\d+(\\.\\d+)?").findAll(this).map { it.value.toDouble() }.toList()
}

fun Int.formatNumber(): String {
    return NumberFormat.getNumberInstance(Locale.US).format(this)
}

fun Long.formatNumber(): String {
    return NumberFormat.getNumberInstance(Locale.US).format(this)
}

fun Int.formatCompact(): String {
    return when {
        this >= 1_000_000_000 -> "${"%.2f".format(this / 1_000_000_000.0)}B"
        this >= 1_000_000 -> "${"%.2f".format(this / 1_000_000.0)}M"
        this >= 1_000 -> "${"%.2f".format(this / 1_000.0)}K"
        else -> this.toString()
    }
}

fun Long.formatCompact(): String {
    return when {
        this >= 1_000_000_000 -> "${"%.2f".format(this / 1_000_000_000.0)}B"
        this >= 1_000_000 -> "${"%.2f".format(this / 1_000_000.0)}M"
        this >= 1_000 -> "${"%.2f".format(this / 1_000.0)}K"
        else -> this.toString()
    }
}

fun Float.dpToPx(density: Float): Float {
    return this * density
}

fun Float.pxToDp(density: Float): Float {
    return this / density
}

fun Int.dpToPx(density: Float): Int {
    return (this * density).roundToInt()
}

fun Int.pxToDp(density: Float): Int {
    return (this / density).roundToInt()
}
