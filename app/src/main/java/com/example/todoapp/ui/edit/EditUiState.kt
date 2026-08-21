package com.example.todoapp.ui.edit

import com.example.todoapp.model.TodoTag

data class EditUiState(
    val title: String = "",
    val description: String = "",
    val targetDate: Long? = null,
    val selectedTag: TodoTag? = null,
)
