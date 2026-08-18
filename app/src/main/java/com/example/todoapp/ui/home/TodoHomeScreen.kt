package com.example.todoapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.R
import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.model.TodoTag
import com.example.todoapp.ui.components.TodoSearchBar
import com.example.todoapp.ui.theme.TodoAppTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TodoHomeScreen(
    onAddTodo: () -> Unit,
    viewModel: TodoHomeViewModel = viewModel(factory = TodoHomeViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TodoHomeScreenContent(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onAddTodo = onAddTodo
    )
}

@Composable
private fun TodoHomeScreenContent(
    uiState: TodoHomeUiState,
    onQueryChange: (String) -> Unit,
    onAddTodo: () -> Unit,
) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH)
    Scaffold(
        topBar = {
            TodoSearchBar(
                query = uiState.searchQuery,
                onQueryChange = onQueryChange
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTodo,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Icon(
                    painterResource(R.drawable.add_24px),
                    contentDescription = null
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.padding(8.dp)
            ) {
                items(uiState.todoEntities) { todo ->
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = todo.targetDate?.format(dateFormatter)
                                    ?: stringResource(R.string.no_date),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = todo.title,
                                fontSize = 24.sp
                            )
                            Text(
                                text = todo.description,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        todo.tag?.let { tag ->
                            TodoTagChip(tag = tag)
                        }
                    }
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun TodoTagChip(tag: TodoTag, modifier: Modifier = Modifier) {
    Text(
        text = tag.name,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = tag.color,
        modifier = modifier
            .background(
                color = tag.color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = tag.color.copy(0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}


private val previewUiState = TodoHomeUiState(
    todoEntities = listOf(
        TodoEntity(
            id = 1,
            title = "Grocery Shopping",
            description = "Visit the local farmers market to pick up fresh seasonal vegetables, organic fruits, and the special sourdough bread that the whole family loves for Sunday brunch.",
            targetDate = LocalDate.now(),
            tag = TodoTag.SHOPPING
        ),
        TodoEntity(
            id = 2,
            title = "Advanced Mathematics",
            description = "Finish the remaining exercises in Chapter 5, then start reviewing the key concepts and formulas in Chapter 6 to prepare for the upcoming midterm exam next Wednesday.",
            targetDate = LocalDate.now().plusDays(1),
            tag = TodoTag.STUDY
        ),
        TodoEntity(
            id = 3,
            title = "Fitness Routine",
            description = "Wake up early at 6 AM for a refreshing 5km run through the central park, followed by a ten-minute cool-down stretch and a healthy, protein-packed breakfast at home.",
            targetDate = LocalDate.now().plusDays(7),
            tag = TodoTag.HEALTH
        ),
        TodoEntity(
            id = 4,
            title = "Project Planning",
            description = "Go through all active projects, check upcoming deadlines, organize tasks for next week, and update the team on the migration progress. Don't forget to review the feedback from the last meeting.",
            targetDate = LocalDate.now().plusDays(20),
            tag = TodoTag.WORK
        )
    )
)

@Preview
@Composable
fun TodoHomeScreenLightPreview() {
    TodoAppTheme(darkTheme = false) {
        TodoHomeScreenContent(
            uiState = previewUiState,
            onAddTodo = {},
            onQueryChange = {},

            )
    }
}

@Preview
@Composable
fun TodoHomeScreenDarkPreview() {
    TodoAppTheme(darkTheme = true) {
        TodoHomeScreenContent(
            uiState = previewUiState,
            onAddTodo = {},
            onQueryChange = {}
        )
    }
}