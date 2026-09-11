package com.example.todoapp.ui

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.todoapp.R
import com.example.todoapp.ui.edit.EditScreen
import com.example.todoapp.ui.entry.AddScreen
import com.example.todoapp.ui.home.HomeScreen
import com.example.todoapp.ui.navigation.AppNavKey
import com.example.todoapp.ui.theme.TodoAppTheme
import kotlinx.coroutines.launch

@Composable
fun TodoApp() {
    val backStack = rememberNavBackStack(AppNavKey.TodoList)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    TodoAppTheme {
        Box {
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
                        HomeScreen(
                            onAddTodo = { backStack.add(AppNavKey.AddTodo) },
                            onEditTodo = { id -> backStack.add(AppNavKey.EditTodo(id)) }
                        )
                    }
                    entry<AppNavKey.AddTodo> {
                        val snackbarMessage = stringResource(R.string.todo_added)
                        AddScreen(
                            onBack = { backStack.removeLastOrNull() },
                            onSaved = {
                                backStack.removeLastOrNull()
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(snackbarMessage)
                                }
                            }
                        )
                    }
                    entry<AppNavKey.EditTodo> { key ->
                        val todoUpdatedMessage = stringResource(R.string.todo_updated)
                        val todoDeletedMessage = stringResource(R.string.todo_deleted)
                        EditScreen(
                            id = key.id,
                            onBack = { backStack.removeLastOrNull() },
                            onUpdated = {
                                backStack.removeLastOrNull()
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(todoUpdatedMessage)
                                }
                            },
                            onDeleted = {
                                backStack.removeLastOrNull()
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(todoDeletedMessage)
                                }
                            }
                        )
                    }
                }
            )
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
            )
        }
    }
}
