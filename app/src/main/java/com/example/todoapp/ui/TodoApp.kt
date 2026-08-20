package com.example.todoapp.ui

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.todoapp.ui.entry.TodoEntryScreen
import com.example.todoapp.ui.home.TodoHomeScreen
import com.example.todoapp.ui.navigation.AppNavKey
import com.example.todoapp.ui.theme.TodoAppTheme

@Composable
fun TodoApp() {
    val backStack = rememberNavBackStack(AppNavKey.TodoList)
    TodoAppTheme {
        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            transitionSpec = {
                slideInHorizontally(initialOffsetX = { it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { -it })
            },
            popTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
            },
            entryProvider = entryProvider {
                entry<AppNavKey.TodoList> {
                    TodoHomeScreen(onAddTodo = { backStack.add(AppNavKey.AddTodo) })
                }
                entry<AppNavKey.AddTodo> {
                    TodoEntryScreen(onBack = { backStack.removeLastOrNull() })
                }
            }
        )
    }
}