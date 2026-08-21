package com.example.todoapp.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppNavKey : NavKey {
    @Serializable
    data object TodoList : AppNavKey

    @Serializable
    data object AddTodo : AppNavKey

    @Serializable
    data class EditTodo(val id: Int) : AppNavKey
}