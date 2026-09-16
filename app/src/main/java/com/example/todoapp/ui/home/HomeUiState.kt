package com.example.todoapp.ui.home

import com.example.todoapp.data.local.TodoEntity


data class OverdueItem(
    val todo: TodoEntity,
    val daysOverdue: Long
)

data class HomeUiState(
    val todayItems: List<TodoEntity> = emptyList(),
    val overdueItems: List<OverdueItem> = emptyList()
)