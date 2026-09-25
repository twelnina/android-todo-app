package com.example.todoapp.ui.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.R
import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.model.PlannedDateFilter
import com.example.todoapp.model.TodoTag
import com.example.todoapp.ui.component.dialogs.PlannedDatePickerDialog
import com.example.todoapp.ui.list.component.TodoItem
import com.example.todoapp.ui.list.component.TodoListControls
import com.example.todoapp.ui.theme.TodoAppTheme
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH)

@Composable
fun ListScreen(
    initialPlannedDateFilter: PlannedDateFilter = PlannedDateFilter.ALL,
    onEdit: (Int) -> Unit,
    onDeleted: (TodoEntity) -> Unit,
    viewModel: ListViewModel = viewModel(
        factory = ListViewModel.createFactory(
            initialPlannedDateFilter
        )
    )
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ListScreenContent(
        uiState = uiState,
        onCheckedChange = viewModel::updateCompleted,
        onPlannedDateChange = viewModel::updatePlannedDate,
        onDelete = { todo ->
            viewModel.deleteTodo(
                todo = todo,
                onDeleted = onDeleted
            )
        },
        onEdit = onEdit,
        onQueryChange = viewModel::onQueryChange,
        onTagSelected = viewModel::onTagSelected,
        onPlannedDateChipClick = viewModel::showBottomSheet,
        onPlannedDateFilterChange = viewModel::onPlannedDateFilterSelected,
        onDismissRequest = viewModel::dismissBottomSheet,
    )
}

@Composable
internal fun ListScreenContent(
    uiState: ListUiState,
    onCheckedChange: (TodoEntity, Boolean) -> Unit,
    onPlannedDateChange: (TodoEntity, LocalDate) -> Unit,
    onDelete: (TodoEntity) -> Unit,
    onEdit: (Int) -> Unit,
    onQueryChange: (String) -> Unit,
    onTagSelected: (TodoTag) -> Unit,
    onPlannedDateChipClick: () -> Unit,
    onPlannedDateFilterChange: (PlannedDateFilter) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val listState = rememberLazyListState()

    var expandedTodoId by rememberSaveable { mutableStateOf<Int?>(null) }

    var dateChangeTodoId by rememberSaveable { mutableStateOf<Int?>(null) }
    val todoForDateChange = uiState.todoGroups
        .asSequence()
        .flatMap { group ->
            group.todos.asSequence()
        }
        .firstOrNull { todo ->
            todo.id == dateChangeTodoId
        }

    val filterKey = Triple(
        uiState.searchQuery,
        uiState.selectedTags,
        uiState.selectedPlannedDateFilter
    )
    val latestFilterKey by rememberUpdatedState(filterKey)
    val hasTodos by rememberUpdatedState(uiState.todoGroups.isNotEmpty())

    val navigationBarPadding =
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    @OptIn(ExperimentalMaterial3Api::class)
    val sheetState = rememberBottomSheetState(initialValue = SheetValue.Hidden)

    LaunchedEffect(listState) {
        snapshotFlow { latestFilterKey }
            .drop(1)
            .collect {
                if (hasTodos) {
                    listState.scrollToItem(0)
                }
            }
    }


    Column(modifier = Modifier.fillMaxSize()) {
        TodoListControls(
            query = uiState.searchQuery,
            selectedPlannedDateFilter = uiState.selectedPlannedDateFilter,
            selectedTags = uiState.selectedTags,
            onQueryChange = onQueryChange,
            onPlannedDateChipClick = onPlannedDateChipClick,
            onTagSelected = onTagSelected
        )

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                bottom = navigationBarPadding + 80.dp,
                start = 16.dp,
                end = 16.dp
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            uiState.todoGroups.forEach { group ->
                stickyHeader(
                    key = group.date ?: "unscheduled",
                    contentType = "date_header"
                ) {
                    TodoDateHeader(group.date)
                }

                itemsIndexed(
                    items = group.todos,
                    key = { _, todo -> todo.id }
                ) { index, todo ->
                    val expanded = expandedTodoId == todo.id

                    TodoItem(
                        todoItemInfo = todo,
                        index = index,
                        count = group.todos.size,
                        expanded = expanded,
                        onCheckedChange = onCheckedChange,
                        onMoreClick = {
                            expandedTodoId = if (expanded) null else todo.id
                        },
                        onChangePlannedDate = {
                            expandedTodoId = null
                            dateChangeTodoId = todo.id
                        },
                        onEdit = {
                            expandedTodoId = null
                            onEdit(todo.id)
                        },
                        onDelete = {
                            expandedTodoId = null
                            onDelete(todo)
                        },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }
        @OptIn(ExperimentalMaterial3Api::class)
        PlannedDateSelectionBottomSheet(
            sheetState = sheetState,
            selectedFilter = uiState.selectedPlannedDateFilter,
            showBottomSheet = uiState.showBottomSheet,
            onDismissRequest = onDismissRequest,
            onPlannedDateFilterChange = onPlannedDateFilterChange
        )
    }

    val today = uiState.today

    if (todoForDateChange != null && today != null) {
        PlannedDatePickerDialog(
            today = today,
            previousDate = todoForDateChange.plannedDate,
            onDismissRequest = {
                dateChangeTodoId = null
            },
            onConfirmRequest = { newDate ->
                onPlannedDateChange(todoForDateChange, newDate)
                dateChangeTodoId = null
            }
        )
    }
}

@Composable
private fun TodoDateHeader(date: LocalDate?, modifier: Modifier = Modifier) {
    val backgroundColor = MaterialTheme.colorScheme.surface

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    0.0f to backgroundColor.copy(alpha = 1.0f),
                    0.75f to backgroundColor.copy(alpha = 0.8f),
                    1.0f to backgroundColor.copy(alpha = 0.6f)
                )
            )
    ) {
        Text(
            text = date?.format(dateFormatter)
                ?: stringResource(R.string.todo_list_filter_unscheduled),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlannedDateSelectionBottomSheet(
    sheetState: SheetState,
    selectedFilter: PlannedDateFilter,
    showBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
    onPlannedDateFilterChange: (PlannedDateFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    if (showBottomSheet) ModalBottomSheet(
        onDismissRequest = onDismissRequest, sheetState = sheetState, modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                stringResource(R.string.todo_list_filter_planned_date),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Transparent,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Column {
                    PlannedDateFilter.entries.forEachIndexed { index, filter ->
                        ListItem(
                            colors = if (filter == selectedFilter) {
                                ListItemDefaults.colors(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    headlineColor = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            } else ListItemDefaults.colors(containerColor = Color.Transparent),
                            modifier = Modifier.clickable {
                                scope.launch {
                                    sheetState.hide()
                                    onPlannedDateFilterChange(filter)
                                }
                            }) {
                            Text(
                                text = stringResource(filter.labelRes),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (index < PlannedDateFilter.entries.size - 1) {
                            HorizontalDivider(
                                thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}


@Preview
@Composable
private fun TodoItemPreview() {
    TodoAppTheme(darkTheme = true) {
        TodoItem(
            todoItemInfo = TodoEntity(
                id = 1,
                title = "Grocery Shopping",
                description = "Visit the local farmers market to pick up fresh seasonal vegetables, organic fruits, and the special sourdough bread that the whole family loves for Sunday brunch.",
                plannedDate = LocalDate.now(),
                tag = TodoTag.SHOPPING
            ),
            index = 0,
            count = 1,
            expanded = false,
            onCheckedChange = { _, _ -> },
            onMoreClick = {},
            onChangePlannedDate = {},
            onDelete = {},
            onEdit = {}
        )
    }
}
