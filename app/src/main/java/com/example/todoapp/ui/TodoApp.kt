package com.example.todoapp.ui

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
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
fun TodoApp(
    viewModel: TodoAppViewModel = viewModel(factory = TodoAppViewModel.Factory)
) {
    val backStack = rememberNavBackStack(AppNavKey.TodoList)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

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
                    HomeScreen(
                        snackbarHostState = snackbarHostState,
                        onAddTodo = {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            backStack.add(AppNavKey.AddTodo)
                        },
                        onEditTodo = { id ->
                            snackbarHostState.currentSnackbarData?.dismiss()
                            backStack.add(AppNavKey.EditTodo(id))
                        }
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
                    val undoLabel = stringResource(R.string.undo)
                    EditScreen(
                        id = key.id,
                        onBack = { backStack.removeLastOrNull() },
                        onUpdated = {
                            backStack.removeLastOrNull()
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(todoUpdatedMessage)
                            }
                        },
                        onDeleted = { deletedTodo ->
                            backStack.removeLastOrNull()
                            coroutineScope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = todoDeletedMessage,
                                    actionLabel = undoLabel,
                                    withDismissAction = true,
                                    duration = SnackbarDuration.Long
                                )

                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.restoreDeletedTodo(deletedTodo)
                                }
                            }
                        }
                    )
                }
            }
        )
    }
}
