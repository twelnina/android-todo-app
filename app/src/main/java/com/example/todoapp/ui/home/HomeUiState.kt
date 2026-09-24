package com.example.todoapp.ui.home

import com.example.todoapp.data.local.TodoEntity
import java.time.LocalDate


data class PastIncompleteItem(
    val todo: TodoEntity,
    val daysSincePlannedDate: Long
)

data class HomeUiState(
    val today: LocalDate? = null,
    val todayItems: List<TodoEntity> = emptyList(),
    val pastIncompleteItems: List<PastIncompleteItem> = emptyList()
)