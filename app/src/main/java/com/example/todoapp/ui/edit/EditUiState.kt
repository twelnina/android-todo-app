package com.example.todoapp.ui.edit

import com.example.todoapp.model.TodoTag
import java.time.LocalDate

data class EditUiState(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val targetDate: LocalDate? = null,
    val selectedTag: TodoTag? = null,
    val isCompleted: Boolean = false
) {
    val isEditValid: Boolean get() = title.isNotBlank() && description.isNotBlank()
}
