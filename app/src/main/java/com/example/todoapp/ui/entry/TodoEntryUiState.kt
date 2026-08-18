package com.example.todoapp.ui.entry

import com.example.todoapp.model.TodoTag

data class TodoEntryUiState(
    val title: String = "",
    val description: String = "",
    val targetDate: Long? = null,
    val label: TodoTag? = null,
    val isEntryValid: Boolean = false
)