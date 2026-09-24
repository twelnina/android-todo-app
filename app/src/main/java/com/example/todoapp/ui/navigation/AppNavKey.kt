package com.example.todoapp.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.example.todoapp.model.PlannedDateFilter
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppNavKey : NavKey {
    @Serializable
    data object TodoHome : AppNavKey

    @Serializable
    data class TodoList(
        val initialPlannedDateFilter: PlannedDateFilter = PlannedDateFilter.ALL
    ) : AppNavKey

    @Serializable
    data object TodoCalendar : AppNavKey

    @Serializable
    data object AddTodo : AppNavKey

    @Serializable
    data class EditTodo(val id: Int) : AppNavKey
}
