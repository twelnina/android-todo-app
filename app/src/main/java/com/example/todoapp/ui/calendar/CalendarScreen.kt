package com.example.todoapp.ui.calendar

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.R
import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.ui.components.CardItem
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
fun CalendarScreen(
    onEditTodo: (Int) -> Unit,
    viewModel: CalendarViewModel = viewModel(factory = CalendarViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    CalendarScreenContent(
        uiState = uiState,
        onDateSelected = viewModel::onDateSelected,
        onPreviousDate = viewModel::onPreviousDate,
        onNextDate = viewModel::onNextDate,
        onCompletedChange = viewModel::updateCompleted,
        onEditTodo = onEditTodo
    )
}

@Composable
private fun CalendarScreenContent(
    uiState: CalendarUiState,
    onDateSelected: (LocalDate) -> Unit,
    onPreviousDate: (LocalDate) -> Unit,
    onNextDate: (LocalDate) -> Unit,
    onCompletedChange: (TodoEntity, Boolean) -> Unit,
    onEditTodo: (Int) -> Unit
) {
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember(currentMonth) { currentMonth.minusMonths(12) }
    val endMonth = remember(currentMonth) { currentMonth.plusMonths(12) }
    val minDate = startMonth.atDay(1)
    val maxDate = endMonth.atEndOfMonth()
    val selectedDate = uiState.selectedDate
    val canGoPrevious = selectedDate?.isAfter(minDate) == true
    val canGoNext = selectedDate?.isBefore(maxDate) == true

    val locale = LocalConfiguration.current.locales[0]
    val formatter = remember(locale) {
        val pattern = DateFormat.getBestDateTimePattern(locale, "MMMd")
        DateTimeFormatter.ofPattern(pattern, locale)
    }

    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        MonthCalendar(
            selectedDate = selectedDate,
            startMonth = startMonth,
            endMonth = endMonth,
            onDateSelected = onDateSelected
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onPreviousDate(minDate) },
                enabled = canGoPrevious
            ) {
                Icon(
                    painter = painterResource(R.drawable.chevron_left_24px),
                    contentDescription = stringResource(R.string.previous_date)
                )
            }
            Text(
                text = uiState.selectedDate?.format(formatter) ?: "",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = { onNextDate(maxDate) },
                enabled = canGoNext
            ) {
                Icon(
                    painter = painterResource(R.drawable.chevron_right_24px),
                    contentDescription = stringResource(R.string.next_date)
                )
            }
        }

        LazyColumn(contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp)) {
            itemsIndexed(
                items = uiState.selectedDateTodos,
                key = { _, todo -> todo.id }
            ) { index, todo ->
                CardItem(
                    todo = todo,
                    showDaysOverdue = false,
                    onCheckedChange = { checked ->
                        onCompletedChange(todo, checked)
                    },
                    onEdit = { onEditTodo(todo.id) }
                )

                if (index < uiState.selectedDateTodos.lastIndex) {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun MonthCalendar(
    selectedDate: LocalDate?,
    startMonth: YearMonth,
    endMonth: YearMonth,
    onDateSelected: (LocalDate) -> Unit
) {
    val currentMonth = remember { YearMonth.now() }
    val daysOfWeek = remember { daysOfWeek() }
    val locale = LocalConfiguration.current.locales[0]
    val monthFormatter = remember(locale) {
        val pattern = DateFormat.getBestDateTimePattern(locale, "yMMM")
        DateTimeFormatter.ofPattern(pattern, locale)
    }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth.coerceIn(startMonth, endMonth),
        firstDayOfWeek = daysOfWeek.first()
    )

    LaunchedEffect(state, selectedDate) {
        val date = selectedDate ?: return@LaunchedEffect
        val selectedMonth = YearMonth.from(date)

        if (selectedMonth in state.startMonth..state.endMonth) {
            state.scrollToMonth(selectedMonth)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        val visibleMonth by remember(state) {
            derivedStateOf { state.firstVisibleMonth.yearMonth }
        }

        Column(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) {
            Text(
                text = visibleMonth.format(monthFormatter),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMediumEmphasized,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalCalendar(
                state = state,
                dayContent = { day ->
                    CalendarItem(
                        day = day,
                        isSelected = day.date == selectedDate,
                        onClick = { onDateSelected(day.date) })
                }
            )
        }
    }
}

@Composable
private fun CalendarItem(day: CalendarDay, isSelected: Boolean, onClick: () -> Unit) {
    val isMonthDate = day.position == DayPosition.MonthDate
    val highlighted = isMonthDate && isSelected

    val backgroundColor = if (highlighted) {
        MaterialTheme.colorScheme.primary
    } else {
        Color.Transparent
    }

    val textColor = when {
        highlighted -> MaterialTheme.colorScheme.onPrimary
        day.date == LocalDate.now() -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(6.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                enabled = day.position == DayPosition.MonthDate,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (day.position == DayPosition.MonthDate)
            Text(
                text = day.date.dayOfMonth.toString(),
                color = textColor
            )
    }
}

@Preview(showBackground = true)
@Composable
private fun MonthCalendarPreview() {
    val currentMonth = YearMonth.now()
    TodoAppTheme {
        MonthCalendar(
            selectedDate = currentMonth.atDay(22),
            startMonth = currentMonth.minusMonths(12),
            endMonth = currentMonth.plusMonths(12),
            onDateSelected = {}
        )
    }
}
