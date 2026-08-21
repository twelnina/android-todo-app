package com.example.todoapp.ui.edit

import com.example.todoapp.model.TodoTag

data class EditUiState(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val targetDate: Long? = null,
    val selectedTag: TodoTag? = null,
) {
    val isEditValid: Boolean get() = title.isNotBlank() && description.isNotBlank()
}
