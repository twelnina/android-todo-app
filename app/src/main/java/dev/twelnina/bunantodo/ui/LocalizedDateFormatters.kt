package dev.twelnina.bunantodo.ui

import android.text.format.DateFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import java.time.format.DateTimeFormatter

@Composable
internal fun rememberMonthDayFormatter(): DateTimeFormatter {
    return rememberLocalizedDateFormatter("MMMd")
}

@Composable
internal fun rememberYearMonthFormatter(): DateTimeFormatter {
    return rememberLocalizedDateFormatter("yMMM")
}

@Composable
internal fun rememberYearMonthDayFormatter(): DateTimeFormatter {
    return rememberLocalizedDateFormatter("yMMMd")
}

@Composable
private fun rememberLocalizedDateFormatter(
    skeleton: String
): DateTimeFormatter {
    val locale = LocalConfiguration.current.locales[0]

    return remember(locale, skeleton) {
        val pattern = DateFormat.getBestDateTimePattern(locale, skeleton)
        DateTimeFormatter.ofPattern(pattern, locale)
    }
}
