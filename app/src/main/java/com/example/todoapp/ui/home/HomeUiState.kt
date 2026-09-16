package com.example.todoapp.ui.home

import com.example.todoapp.data.local.TodoEntity

enum class TodoStatus {
    Active,
    OverDue,
    Completed
}

data class HomeUiState(
    val todayItems: List<TodoEntity> = emptyList(),
    val overdueItems: List<TodoEntity> = emptyList()
)