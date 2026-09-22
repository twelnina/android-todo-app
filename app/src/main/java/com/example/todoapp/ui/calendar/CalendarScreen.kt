package com.example.todoapp.ui.calendar

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todoapp.ui.theme.TodoAppTheme
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Composable
fun CalendarScreen() {
    CalendarScreenContent()
}

@Composable
private fun CalendarScreenContent() {
    MonthCalendar()
}

@Composable
private fun MonthCalendar() {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(12) }
    val endMonth = remember { currentMonth.plusMonths(12) }
    val daysOfWeek = remember { daysOfWeek() }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    val locale = LocalConfiguration.current.locales[0]
    val monthFormatter = remember(locale) {
        val pattern = DateFormat.getBestDateTimePattern(locale, "yMMM")
        DateTimeFormatter.ofPattern(pattern, locale)
    }
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            )
        ) {
            val visibleMonth by remember {
                derivedStateOf { state.firstVisibleMonth.yearMonth }
            }

            Text(
                text = visibleMonth.format(monthFormatter),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMediumEmphasized,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalCalendar(
                state = state,
                dayContent = { day ->
                    CalendarItem(day)
                }
            )
        }
    }
}

@Composable
private fun CalendarItem(day: CalendarDay) {
    Box(
        modifier = Modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        if (day.position == DayPosition.MonthDate)
            Text(
                text = day.date.dayOfMonth.toString(),
                color = if (day.date == LocalDate.now()) {
                    MaterialTheme.colorScheme.primary
                } else MaterialTheme.colorScheme.onSurface
            )
    }
}

@Preview(showBackground = true)
@Composable
private fun MonthCalendarPreview() {
    TodoAppTheme {
        MonthCalendar()
    }
}
