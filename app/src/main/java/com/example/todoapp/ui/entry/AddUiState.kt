package com.example.todoapp.ui.entry

import com.example.todoapp.model.TodoTag

data class AddUiState(
    val title: String = "",
    val description: String = "",
    val targetDate: Long? = null,
    val selectedTag: TodoTag? = null,
    val isEntryValid: Boolean = false
)