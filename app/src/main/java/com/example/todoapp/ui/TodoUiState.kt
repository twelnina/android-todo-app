package com.example.todoapp.ui

import com.example.todoapp.TodoItem

data class TodoUiState(
    val searchQuery: String = "",
    val todoItems: List<TodoItem> = emptyList()
)
