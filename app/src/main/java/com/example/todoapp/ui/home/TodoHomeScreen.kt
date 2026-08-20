package com.example.todoapp.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.todoapp.model.DueDateFilter
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
        onTagSelected = viewModel::onTagSelected,
        onDueDateChipClick = viewModel::showBottomSheet,
        onDueDateFilterChange = viewModel::onDueDateFilterSelected,
        onDismissRequest = viewModel::dismissBottomSheet,
        onAddTodo = onAddTodo
    )
}

@Composable
private fun TodoHomeScreenContent(
    uiState: TodoHomeUiState,
    onQueryChange: (String) -> Unit,
    onTagSelected: (TodoTag) -> Unit,
    onDueDateChipClick: () -> Unit,
    onDueDateFilterChange: (DueDateFilter) -> Unit,
    onDismissRequest: () -> Unit,
    onAddTodo: () -> Unit,
) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH)
    val listState = rememberLazyListState()
    @OptIn(ExperimentalMaterial3Api::class) val sheetState = rememberModalBottomSheetState()

    LaunchedEffect(uiState.todoEntities) {
        if (uiState.todoEntities.isNotEmpty()) {
            listState.scrollToItem(0)
        }
    }

    Scaffold(
        topBar = {
            Column {
                TodoSearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { newQuery ->
                        onQueryChange(newQuery)
                    }
                )
                Spacer(modifier = Modifier.padding(vertical = 2.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        val isAllSelected = uiState.selectedDueDateFilter == DueDateFilter.ALL
                        FilterChip(
                            selected = !isAllSelected,
                            label = {
                                Text(
                                    if (isAllSelected)
                                        stringResource(R.string.due_date)
                                    else stringResource(uiState.selectedDueDateFilter.labelRes)
                                )
                            },
                            leadingIcon = if (!isAllSelected) {
                                {
                                    Icon(
                                        painter = painterResource(R.drawable.check_24px),
                                        contentDescription = null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else null,
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(R.drawable.arrow_drop_down_24px),
                                    contentDescription = null,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            },
                            onClick = onDueDateChipClick
                        )
                    }
                    items(TodoTag.entries) { tag ->
                        FilterChip(
                            selected = uiState.selectedTags.contains(tag),
                            label = {
                                Text(
                                    tag.name.lowercase().replaceFirstChar { it.uppercase() }
                                )
                            },
                            leadingIcon = if (uiState.selectedTags.contains(tag)) {
                                {
                                    Icon(
                                        painter = painterResource(R.drawable.check_24px),
                                        contentDescription = null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else null,
                            onClick = { onTagSelected(tag) }
                        )
                    }
                }
            }
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
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                bottom = innerPadding.calculateBottomPadding() + 80.dp,
                start = 8.dp,
                end = 8.dp
            ),
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            items(
                items = uiState.todoEntities,
                key = { it.id }
            ) { todo ->
                Row(
                    modifier = Modifier
                        .padding(vertical = 16.dp, horizontal = 8.dp)
                        .animateItem(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = todo.targetDate?.let { "~ ${it.format(dateFormatter)}" }
                                ?: stringResource(R.string.no_date),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = todo.title,
                            fontSize = 22.sp
                        )
                        Text(
                            text = todo.description,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
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
        @OptIn(ExperimentalMaterial3Api::class)
        DueDateSelectionBottomSheet(
            sheetState = sheetState,
            selectedFilter = uiState.selectedDueDateFilter,
            showBottomSheet = uiState.showBottomSheet,
            onDismissRequest = onDismissRequest,
            onDueDateFilterChange = onDueDateFilterChange
        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DueDateSelectionBottomSheet(
    sheetState: SheetState,
    selectedFilter: DueDateFilter,
    showBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
    onDueDateFilterChange: (DueDateFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    if (showBottomSheet)
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            modifier = modifier
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    stringResource(R.string.due_date),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Column {
                        DueDateFilter.entries.forEach { filter ->
                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = stringResource(filter.labelRes),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                colors = if (filter == selectedFilter) {
                                    ListItemDefaults.colors(
                                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                        headlineColor = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                } else ListItemDefaults.colors(),
                                modifier = Modifier
                                    .clickable { onDueDateFilterChange(filter) }
                                    .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
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
            onTagSelected = {},
            onDueDateChipClick = {},
            onDueDateFilterChange = {},
            onDismissRequest = {},
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
            onTagSelected = {},
            onDueDateChipClick = {},
            onDueDateFilterChange = {},
            onDismissRequest = {},
            onQueryChange = {}
        )
    }
}