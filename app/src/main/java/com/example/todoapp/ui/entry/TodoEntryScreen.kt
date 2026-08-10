package com.example.todoapp.ui.entry

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todoapp.R
import com.example.todoapp.model.TodoTag
import com.example.todoapp.ui.theme.TodoAppTheme

@Composable
fun TodoEntryScreenContent(
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
                value = "",
                onValueChange = {},
                label = {
                    Text(text = stringResource(R.string.title))
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
            TextField(
                value = "",
                onValueChange = {},
                label = { Text(text = stringResource(R.string.description)) },
                minLines = 5,
                modifier = Modifier.fillMaxWidth()
            )
            DateTextField(
                onDateSelected = {},
                onDismiss = {},
                modifier = Modifier.fillMaxWidth()
            )
            TagSelector(
                selectedTag = TodoTag.STUDY,
                onTagSelected = {},
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Done")
            }

        }
    }
}

@Composable
private fun DateTextField(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    TextField(
        value = "",
        onValueChange = {},
        readOnly = true,
        label = { Text(stringResource(R.string.target_date)) },
        trailingIcon = {
            Icon(
                painter = painterResource(R.drawable.calendar_today_24px),
                contentDescription = null
            )
        },
        modifier = modifier.clickable { showDatePicker = true }
    )

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                }
                ) { Text(stringResource(R.string.ok)) }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
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
    selectedTag: TodoTag,
    onTagSelected: (TodoTag) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.tags),
            style = MaterialTheme.typography.titleSmall
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TodoTag.entries.forEach { tag ->
                FilterChip(
                    selected = selectedTag == tag,
                    onClick = { onTagSelected(tag) },
                    label = { Text(tag.name) },
                    leadingIcon = if (selectedTag == tag) {
                        { Icon(painterResource(R.drawable.check_24px), null) }
                    } else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = tag.color
                    )
                )
            }
        }
    }
}


@Preview
@Composable
fun TodoEntryScreenContentLightPreview() {
    TodoAppTheme(darkTheme = false) {
        TodoEntryScreenContent(onBack = {})
    }
}

@Preview
@Composable
fun TodoEntryScreenContentDarkPreview() {
    TodoAppTheme(darkTheme = true) {
        TodoEntryScreenContent(onBack = {})
    }
}