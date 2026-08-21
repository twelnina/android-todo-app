package com.example.todoapp.ui.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
fun EditScreen(
    id: Int,
    onBack: () -> Unit,
    viewModel: EditViewModel = viewModel(factory = EditViewModel.Factory),
) {
    LaunchedEffect(id) { viewModel.loadItem(id) }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    EditScreenContent(
        uiState = uiState,
        onTitleChange = viewModel::updateTitle,
        onDescriptionChange = viewModel::updateDescription,
        onTargetDateChange = viewModel::updateTargetDate,
        onTagChange = viewModel::updateSelectedTag,
        onBack = onBack
    )
}

@Composable
private fun EditScreenContent(
    uiState: EditUiState,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTargetDateChange: (Long?) -> Unit,
    onTagChange: (TodoTag?) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            TopAppBar(
                title = { Text(stringResource(R.string.edit_todo)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource((R.drawable.arrow_back_24px)),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(R.drawable.delete_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.delete),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.done))
                }

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
fun EditScreenPreview() {
    TodoAppTheme(darkTheme = true) {
        EditScreenContent(EditUiState(), {}, {}, {}, {}, {})
    }
}