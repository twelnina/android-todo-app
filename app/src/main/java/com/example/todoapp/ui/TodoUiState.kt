package com.example.todoapp.ui

import com.example.todoapp.model.TodoModel
import com.example.todoapp.model.TodoTag
import java.time.LocalDate

data class TodoUiState(
    val searchQuery: String = "",
    val todoItems: List<TodoModel> = listOf(
        TodoModel(
            title = "Go to the library",
            detail = "I'll read a book Fact Fullness!",
            date = LocalDate.now(),
            label = TodoTag.STUDY
        )
    )
)
