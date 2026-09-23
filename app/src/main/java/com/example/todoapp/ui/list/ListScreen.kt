package com.example.todoapp.ui.list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.todoapp.ui.components.TagChip
import com.example.todoapp.ui.components.TodoSearchBar
import com.example.todoapp.ui.theme.TodoAppTheme
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH)

@Composable
fun ListScreen(
    onEditTodo: (Int) -> Unit,
    initialDueDateFilter: DueDateFilter = DueDateFilter.ALL,
    viewModel: ListViewModel = viewModel(factory = ListViewModel.createFactory(initialDueDateFilter))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ListScreenContent(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onTagSelected = viewModel::onTagSelected,
        onDueDateChipClick = viewModel::showBottomSheet,
        onDueDateFilterChange = viewModel::onDueDateFilterSelected,
        onDismissRequest = viewModel::dismissBottomSheet,
        onEditTodo = onEditTodo
    )
}

@Composable
internal fun ListScreenContent(
    uiState: ListUiState,
    onQueryChange: (String) -> Unit,
    onTagSelected: (TodoTag) -> Unit,
    onDueDateChipClick: () -> Unit,
    onDueDateFilterChange: (DueDateFilter) -> Unit,
    onDismissRequest: () -> Unit,
    onEditTodo: (Int) -> Unit
) {
    val listState = rememberLazyListState()

    val filterKey = Triple(
        uiState.searchQuery,
        uiState.selectedTags,
        uiState.selectedDueDateFilter
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
        TodoSearchBar(
            query = uiState.searchQuery,
            onQueryChange = { newQuery ->
                onQueryChange(newQuery)
            },
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.padding(vertical = 2.dp))

        TodoFilterRow(
            selectedDueDateFilter = uiState.selectedDueDateFilter,
            selectedTags = uiState.selectedTags,
            onDueDateChipClick = onDueDateChipClick,
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
                    key = group.date ?: "no_date",
                    contentType = "date_header"
                ) {
                    TodoDateHeader(group.date)
                }

                itemsIndexed(
                    items = group.todos,
                    key = { _, todo -> todo.id }
                ) { index, todo ->
                    TodoItem(
                        todoItemInfo = todo,
                        index = index,
                        count = group.todos.size,
                        onEditTodo = onEditTodo,
                        modifier = Modifier.animateItem()
                    )
                }
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
private fun TodoFilterRow(
    selectedDueDateFilter: DueDateFilter,
    selectedTags: Set<TodoTag>,
    onDueDateChipClick: () -> Unit,
    onTagSelected: (TodoTag) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item {
            val isAllSelected = selectedDueDateFilter == DueDateFilter.ALL
            FilterChip(
                selected = !isAllSelected, label = {
                    Text(
                        if (isAllSelected) stringResource(R.string.due_date)
                        else stringResource(selectedDueDateFilter.labelRes)
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
                } else null, trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.arrow_drop_down_24px),
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }, onClick = onDueDateChipClick
            )
        }
        items(TodoTag.entries) { tag ->
            FilterChip(
                selected = selectedTags.contains(tag),
                label = { Text(stringResource(tag.labelRes)) },
                leadingIcon = if (selectedTags.contains(tag)) {
                    {
                        Icon(
                            painter = painterResource(R.drawable.check_24px),
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else null,
                onClick = { onTagSelected(tag) })
        }
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
            text = date?.format(dateFormatter) ?: stringResource(R.string.no_date),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun TodoItem(
    todoItemInfo: TodoEntity,
    index: Int,
    count: Int,
    onEditTodo: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    SegmentedListItem(
        onClick = { onEditTodo(todoItemInfo.id) },
        shapes = ListItemDefaults.segmentedShapes(index, count),
        colors = ListItemDefaults.segmentedColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(bottom = ListItemDefaults.SegmentedGap),
        supportingContent = {
            Text(
                text = todoItemInfo.description,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        },
        trailingContent = {
            todoItemInfo.tag?.let { tag ->
                TagChip(
                    tag = tag,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    ) {
        Text(
            text = todoItemInfo.title,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(vertical = 4.dp)
        )
    }
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
    val scope = rememberCoroutineScope()

    if (showBottomSheet) ModalBottomSheet(
        onDismissRequest = onDismissRequest, sheetState = sheetState, modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                stringResource(R.string.due_date), style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Transparent,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Column {
                    DueDateFilter.entries.forEachIndexed { index, filter ->
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
                                    onDueDateFilterChange(filter)
                                }
                            }) {
                            Text(
                                text = stringResource(filter.labelRes),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (index < DueDateFilter.entries.size - 1) {
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
                targetDate = LocalDate.now(),
                tag = TodoTag.SHOPPING
            ),
            index = 0,
            count = 1,
            onEditTodo = {}
        )
    }
}
