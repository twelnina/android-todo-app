package com.example.todoapp.ui.list

import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.model.DueDateFilter
import com.example.todoapp.model.TodoTag
import java.time.LocalDate

data class TodoDateGroup(
    val date: LocalDate?,
    val todos: List<TodoEntity>
)

data class ListUiState(
    val searchQuery: String = "",
    val selectedTags: Set<TodoTag> = emptySet(),
    val selectedDueDateFilter: DueDateFilter = DueDateFilter.ALL,
    val showBottomSheet: Boolean = false,
    val todoGroups: List<TodoDateGroup> = emptyList()
)
