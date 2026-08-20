package com.example.todoapp.ui.home

import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.model.DueDateFilter
import com.example.todoapp.model.TodoTag

data class TodoHomeUiState(
    val searchQuery: String = "",
    val selectedTags: Set<TodoTag> = emptySet(),
    val selectedDueDateFilter: DueDateFilter = DueDateFilter.ALL,
    val showBottomSheet: Boolean = false,
    val todoEntities: List<TodoEntity> = emptyList()
)