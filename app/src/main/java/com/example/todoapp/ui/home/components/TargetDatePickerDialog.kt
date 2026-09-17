package com.example.todoapp.ui.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.getSelectedDate
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.todoapp.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
internal fun TargetDatePickerDialog(
    previousDate: LocalDate?,
    onConfirmRequest: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val today = LocalDate.now()

    val datePickerState = rememberDatePickerState(
        initialSelectedDate = null,
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

    val newDate = datePickerState.getSelectedDate()

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                enabled = newDate != null,
                onClick = {
                    newDate?.let(onConfirmRequest)
                }
            ) { Text(stringResource(R.string.ok)) }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel))
            }
        }
    ) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = stringResource(R.string.reschedule),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(20.dp))
                DateChangeSummary(
                    previousDate = previousDate,
                    newDate = newDate,
                )
                HorizontalDivider(modifier = Modifier.padding(top = 16.dp))
            }
            DatePicker(
                state = datePickerState,
                modifier = Modifier.fillMaxWidth(),
                title = null,
                headline = null,
                showModeToggle = false
            )
        }
    }
}
