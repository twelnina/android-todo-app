package com.example.todoapp.ui.entry

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.example.todoapp.ui.component.TodoEntryBody
import com.example.todoapp.ui.theme.TodoAppTheme
import java.time.LocalDate

@Composable
fun AddScreen(
    viewModel: AddViewModel = viewModel(factory = AddViewModel.Factory),
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AddScreenContent(
        uiState = uiState,
        onTitleChange = viewModel::updateTitle,
        onDescriptionChange = viewModel::updateDescription,
        onPlannedDateChange = viewModel::updatePlannedDate,
        onTagChange = viewModel::updateSelectedTag,
        onDone = { viewModel.saveTodo(onSaved = onSaved) },
        onBack = onBack
    )
}

@Composable
private fun AddScreenContent(
    uiState: AddUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPlannedDateChange: (LocalDate?) -> Unit,
    onTagChange: (TodoTag) -> Unit,
    onDone: () -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(stringResource(R.string.todo_add_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back_24px),
                            contentDescription = stringResource(R.string.common_action_back)
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
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp)
            ) {
                Text(stringResource(R.string.todo_editor_action_done))
            }
        }
    ) { innerPadding ->
        TodoEntryBody(
            today = uiState.today,
            title = uiState.title,
            description = uiState.description,
            plannedDate = uiState.plannedDate,
            selectedTag = uiState.selectedTag,
            onTitleChange = onTitleChange,
            onDescriptionChange = onDescriptionChange,
            onDateChange = onPlannedDateChange,
            onTagChange = onTagChange,
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        )
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
            onPlannedDateChange = {},
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
            onPlannedDateChange = {},
            onTagChange = {},
            onDone = {},
            onBack = {},
        )
    }
}
