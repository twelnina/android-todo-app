package com.example.todoapp.ui.edit.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun TargetDatePicker(
    selectedDate: LocalDate?,
    onDateChange: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()

    val datePickerState = rememberDatePickerState(
        initialSelectedDate = selectedDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant
                    .ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate()

                return date >= today
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year >= today.year
            }
        }
    )
    val pickedDate = datePickerState.getSelectedDate()

    LaunchedEffect(pickedDate) {
        if (pickedDate != selectedDate) {
            onDateChange(pickedDate)
        }
    }


    DatePicker(
        state = datePickerState,
        modifier = modifier.clip(MaterialTheme.shapes.large),
        colors = DatePickerDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        title = null,
        headline = null,
        showModeToggle = false
    )
}