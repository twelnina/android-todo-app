package com.example.todoapp.ui.entry

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.R
import com.example.todoapp.model.TodoTag
import com.example.todoapp.ui.components.TodoEntryBody
import com.example.todoapp.ui.theme.TodoAppTheme

@Composable
fun AddScreen(
    viewModel: AddViewModel = viewModel(factory = AddViewModel.Factory),
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddScreenContent(
        uiState = uiState,
        onTitleChange = viewModel::updateTitle,
        onDescriptionChange = viewModel::updateDescription,
        onTargetDateChange = viewModel::updateTargetDate,
        onTagChange = viewModel::updateSelectedTag,
        onDone = { viewModel.saveTodo(onSaved = onBack) },
        onBack = onBack
    )
}

@Composable
private fun AddScreenContent(
    uiState: AddUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTargetDateChange: (Long?) -> Unit,
    onTagChange: (TodoTag) -> Unit,
    onDone: () -> Unit,
    onBack: () -> Unit,
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
        },
        bottomBar = {
            Button(
                onClick = onDone,
                enabled = uiState.isEntryValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(stringResource(R.string.done))
            }
        }
    ) { innerPadding ->
        Column {
            TodoEntryBody(
                title = uiState.title,
                description = uiState.description,
                targetDate = uiState.targetDate,
                selectedTag = uiState.selectedTag,
                onTitleChange = onTitleChange,
                onDescriptionChange = onDescriptionChange,
                onTargetDateChange = onTargetDateChange,
                onTagChange = onTagChange,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}


@Preview
@Composable
fun AddScreenContentLightPreview() {
    val previewUiState = AddUiState(selectedTag = TodoTag.STUDY, isEntryValid = true)
    TodoAppTheme(darkTheme = false) {
        AddScreenContent(
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
fun AddScreenContentDarkPreview() {
    val previewUiState = AddUiState(selectedTag = TodoTag.STUDY)
    TodoAppTheme(darkTheme = true) {
        AddScreenContent(
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