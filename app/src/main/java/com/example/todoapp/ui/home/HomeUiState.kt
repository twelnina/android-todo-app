package com.example.todoapp.ui.home

import com.example.todoapp.data.local.TodoEntity
import java.time.LocalDate


data class OverdueItem(
    val todo: TodoEntity,
    val daysOverdue: Long
)

data class HomeUiState(
    val today: LocalDate? = null,
    val todayItems: List<TodoEntity> = emptyList(),
    val overdueItems: List<OverdueItem> = emptyList()
)