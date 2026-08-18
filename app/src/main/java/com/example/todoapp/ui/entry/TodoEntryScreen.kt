package com.example.todoapp.ui.entry

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.R
import com.example.todoapp.model.TodoTag
import com.example.todoapp.ui.theme.TodoAppTheme
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TodoEntryScreen(
    viewModel: TodoEntryViewModel = viewModel(factory = TodoEntryViewModel.Factory),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TodoEntryScreenContent(
        uiState = uiState,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onTargetDateChange = viewModel::onTargetDateChange,
        onTagChange = viewModel::onTagChange,
        onDone = { viewModel.saveTodo(onSaved = onBack) },
        onBack = onBack
    )
}

@Composable
fun TodoEntryScreenContent(
    uiState: TodoEntryUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTargetDateChange: (Long?) -> Unit,
    onTagChange: (TodoTag) -> Unit,
    onDone: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(stringResource(R.string.add_todo)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_24px),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            TextField(
                value = uiState.title,
                onValueChange = onTitleChange,
                label = { Text(text = stringResource(R.string.title)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = uiState.description,
                onValueChange = onDescriptionChange,
                label = { Text(text = stringResource(R.string.description)) },
                minLines = 5,
                modifier = Modifier.fillMaxWidth()
            )
            DateTextField(
                onDateSelected = onTargetDateChange,
                selectedDate = uiState.targetDate,
                modifier = Modifier.fillMaxWidth()
            )
            TagSelector(
                onTagChange = onTagChange,
                selectedTag = uiState.tag,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onDone,
                enabled = uiState.isEntryValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.done))
            }

        }
    }
}

@Composable
private fun DateTextField(
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

@Composable
private fun TagSelector(
    selectedTag: TodoTag?,
    onTagChange: (TodoTag) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.tags),
            style = MaterialTheme.typography.titleSmall
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            TodoTag.entries.forEach { tag ->
                FilterChip(
                    selected = selectedTag == tag,
                    onClick = { onTagChange(tag) },
                    label = { Text(tag.name) },
                    leadingIcon = if (selectedTag == tag) {
                        { Icon(painterResource(R.drawable.check_24px), null) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = tag.color)
                )
            }
        }
    }
}


@Preview
@Composable
fun TodoEntryScreenContentLightPreview() {
    val previewUiState = TodoEntryUiState(tag = TodoTag.STUDY)
    TodoAppTheme(darkTheme = false) {
        TodoEntryScreenContent(
            uiState = previewUiState,
            onTitleChange = {},
            onDescriptionChange = {},
            onTargetDateChange = {},
            onTagChange = {},
            onDone = {},
            onBack = {},
        )
    }
}

@Preview
@Composable
fun TodoEntryScreenContentDarkPreview() {
    val previewUiState = TodoEntryUiState(tag = TodoTag.STUDY)
    TodoAppTheme(darkTheme = true) {
        TodoEntryScreenContent(
            uiState = previewUiState,
            onTitleChange = {},
            onDescriptionChange = {},
            onTargetDateChange = {},
            onTagChange = {},
            onDone = {},
            onBack = {},
        )
    }
}