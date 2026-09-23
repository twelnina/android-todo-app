package com.example.todoapp.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.example.todoapp.model.DueDateFilter
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppNavKey : NavKey {
    @Serializable
    data object TodoHome : AppNavKey

    @Serializable
    data class TodoList(
        val initialDueDateFilter: DueDateFilter = DueDateFilter.ALL
    ) : AppNavKey

    @Serializable
    data object TodoCalendar : AppNavKey

    @Serializable
    data object AddTodo : AppNavKey

    @Serializable
    data class EditTodo(val id: Int) : AppNavKey
}