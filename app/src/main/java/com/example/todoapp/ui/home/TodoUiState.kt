package com.example.todoapp.ui.home

import com.example.todoapp.data.local.TodoEntity

data class TodoUiState(
    val searchQuery: String = "",
    val todoEntities: List<TodoEntity> = emptyList()
)