package com.example.todoapp.ui.list.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.todoapp.R
import com.example.todoapp.model.PlannedDateFilter
import com.example.todoapp.model.TodoTag
import com.example.todoapp.ui.theme.TodoAppTheme

@Composable
internal fun TodoListControls(
    query: String,
    selectedPlannedDateFilter: PlannedDateFilter,
    selectedTags: Set<TodoTag>,
    onQueryChange: (String) -> Unit,
    onPlannedDateChipClick: () -> Unit,
    onTagSelected: (TodoTag) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        TodoSearchBar(
            query = query,
            onQueryChange = onQueryChange,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        TodoFilterRow(
            selectedPlannedDateFilter = selectedPlannedDateFilter,
            selectedTags = selectedTags,
            onPlannedDateChipClick = onPlannedDateChipClick,
            onTagSelected = onTagSelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodoSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    SearchBar(
        modifier = modifier.fillMaxWidth(),
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = {},
                expanded = false,
                onExpandedChange = {},
                placeholder = {
                    Text(
                        text = stringResource(R.string.search),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            )
        },
        expanded = false,
        onExpandedChange = {}
    ) {}
}

@Composable
private fun TodoFilterRow(
    selectedPlannedDateFilter: PlannedDateFilter,
    selectedTags: Set<TodoTag>,
    onPlannedDateChipClick: () -> Unit,
    onTagSelected: (TodoTag) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        item {
            val isAllSelected = selectedPlannedDateFilter == PlannedDateFilter.ALL

            FilterChip(
                selected = !isAllSelected,
                label = {
                    Text(
                        text = if (isAllSelected) {
                            stringResource(R.string.planned_date)
                        } else {
                            stringResource(selectedPlannedDateFilter.labelRes)
                        }
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
                } else {
                    null
                },
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.arrow_drop_down_24px),
                        contentDescription = null,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                },
                onClick = onPlannedDateChipClick
            )
        }

        items(
            items = TodoTag.entries,
            key = { tag -> tag.name }
        ) { tag ->
            val selected = tag in selectedTags

            FilterChip(
                selected = selected,
                label = { Text(stringResource(tag.labelRes)) },
                leadingIcon = if (selected) {
                    {
                        Icon(
                            painter = painterResource(R.drawable.check_24px),
                            contentDescription = null,
                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                        )
                    }
                } else {
                    null
                },
                onClick = { onTagSelected(tag) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TodoListControlsPreview() {
    TodoAppTheme {
        TodoListControls(
            query = "Kotlin",
            selectedPlannedDateFilter = PlannedDateFilter.TODAY,
            selectedTags = setOf(TodoTag.WORK),
            onQueryChange = {},
            onPlannedDateChipClick = {},
            onTagSelected = {}
        )
    }
}
