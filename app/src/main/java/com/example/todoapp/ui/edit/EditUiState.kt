package com.example.todoapp.ui.edit

import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.model.TodoTag
import java.time.LocalDate

data class EditUiState(
    val today: LocalDate? = null,
    val isLoaded: Boolean = false,
    val originalTodo: TodoEntity? = null,

    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val targetDate: LocalDate? = null,
    val selectedTag: TodoTag? = null,

    val isCompleted: Boolean = false
) {
    val hasRequiredFields: Boolean get() = title.isNotBlank() && description.isNotBlank()

    val hasChanges: Boolean
        get() = originalTodo != null && (
                title != originalTodo.title ||
                        description != originalTodo.description ||
                        targetDate != originalTodo.targetDate ||
                        selectedTag != originalTodo.tag
                )
}
