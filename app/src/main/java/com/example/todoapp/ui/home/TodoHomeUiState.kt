package com.example.todoapp.ui.home

import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.model.TodoTag

data class TodoHomeUiState(
    val searchQuery: String = "",
    val selectedTags: Set<TodoTag> = emptySet(),
    val todoEntities: List<TodoEntity> = emptyList()
)