package com.example.todoapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.todoapp.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun DateTextField(
    onDateSelected: (Long?) -> Unit,
    selectedDate: Long?,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateText = selectedDate?.let { millis ->
        Instant
            .ofEpochMilli(millis)
            .atZone(ZoneOffset.UTC)
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault()))
    } ?: ""

    Box(modifier = Modifier.clickable { showDatePicker = true }) {
        TextField(
            value = dateText,
            onValueChange = { },
            enabled = false,
            readOnly = true,
            label = { Text(stringResource(R.string.target_date)) },
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.calendar_today_24px),
                    contentDescription = null
                )
            },
            colors = TextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledIndicatorColor = MaterialTheme.colorScheme.outline
            ),
            modifier = modifier,
        )
    }

    if (showDatePicker) {
        val today = LocalDate.now()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate,
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

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    showDatePicker = false
                }
                ) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}