package com.example.todoapp.ui.home.components

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.todoapp.R
import com.example.todoapp.data.local.TodoEntity
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
internal fun TargetDatePickerDialog(
    todo: TodoEntity,
    onConfirmRequest: (TodoEntity, LocalDate) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val today = LocalDate.now()

    val datePickerState = rememberDatePickerState(
        initialSelectedDate = todo.targetDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                val date = Instant
                    .ofEpochMilli(utcTimeMillis)
                    .atZone(ZoneOffset.UTC)
                    .toLocalDate()
                return date >= today
            }
        }
    )

    val selectedDate = datePickerState.getSelectedDate()
    val canConfirm = selectedDate?.let { it >= today } == true

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.getSelectedDate()?.let { selectedDate ->
                        onConfirmRequest(todo, selectedDate)
                    }
                },
                enabled = canConfirm
            ) { Text(stringResource(R.string.ok)) }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        DatePicker(
            state = datePickerState,
            title = null,
            headline = null,
            showModeToggle = false
        )
    }
}
