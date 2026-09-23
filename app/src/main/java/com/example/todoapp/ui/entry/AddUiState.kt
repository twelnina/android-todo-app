package com.example.todoapp.ui.entry

import com.example.todoapp.model.TodoTag
import java.time.LocalDate

data class AddUiState(
    val today: LocalDate? = null,

    val title: String = "",
    val description: String = "",
    val targetDate: LocalDate? = null,
    val selectedTag: TodoTag? = null,
    val isEntryValid: Boolean = false
)
