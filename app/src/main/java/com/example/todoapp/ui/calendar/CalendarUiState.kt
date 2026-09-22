package com.example.todoapp.ui.calendar

import com.example.todoapp.data.local.TodoEntity
import java.time.LocalDate

data class CalendarUiState(
    val today: LocalDate? = null,
    val selectedDate: LocalDate? = null,
    val todos: List<TodoEntity> = emptyList(),
    val selectedDateTodos: List<TodoEntity> = emptyList()
)
