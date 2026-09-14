package com.example.todoapp.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.todoapp.R
import com.example.todoapp.ui.calendar.CalendarScreen
import com.example.todoapp.ui.edit.EditScreen
import com.example.todoapp.ui.entry.AddScreen
import com.example.todoapp.ui.home.HomeScreen
import com.example.todoapp.ui.list.ListScreen
import com.example.todoapp.ui.navigation.AppNavKey
import com.example.todoapp.ui.theme.TodoAppTheme
import kotlinx.coroutines.launch

@Composable
fun TodoApp(
    viewModel: TodoAppViewModel = viewModel(factory = TodoAppViewModel.Factory)
) {
    val backStack = rememberNavBackStack(AppNavKey.TodoHome)
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val currentNavKey = backStack.lastOrNull()

    val showToolbar = when (currentNavKey) {
        AppNavKey.TodoHome,
        AppNavKey.TodoList,
        AppNavKey.TodoCalendar -> true

        else -> false
    }

    TodoAppTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            floatingActionButton = {
                if (showToolbar) {
                    HorizontalFloatingToolbar(
                        expanded = true,
                        modifier = Modifier.height(56.dp),
                        floatingActionButton = {
                            FloatingToolbarDefaults.VibrantFloatingActionButton(
                                shape = CircleShape,
                                onClick = { backStack.add(AppNavKey.AddTodo) }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.add_24px),
                                    contentDescription = stringResource(R.string.add_todo)
                                )
                            }
                        }
                    ) {
                        FloatingToolbarItem(
                            selected = currentNavKey == AppNavKey.TodoHome,
                            iconResourceId = R.drawable.home_24px,
                            stringResourceId = R.string.home,
                            onClick = { backStack.navigateToTopLevel(AppNavKey.TodoHome) }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        FloatingToolbarItem(
                            selected = currentNavKey == AppNavKey.TodoList,
                            iconResourceId = R.drawable.list_24px,
                            stringResourceId = R.string.list,
                            onClick = { backStack.navigateToTopLevel(AppNavKey.TodoList) }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        FloatingToolbarItem(
                            selected = currentNavKey == AppNavKey.TodoCalendar,
                            iconResourceId = R.drawable.calendar_month_24px,
                            stringResourceId = R.string.calendar,
                            onClick = { backStack.navigateToTopLevel(AppNavKey.TodoCalendar) }
                        )
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            NavDisplay(
                backStack = backStack,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .consumeWindowInsets(it),
                onBack = { backStack.removeLastOrNull() },
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {
                    entry<AppNavKey.TodoHome> {
                        HomeScreen()
                    }
                    entry<AppNavKey.TodoList> {
                        ListScreen(
                            onEditTodo = { id ->
                                snackbarHostState.currentSnackbarData?.dismiss()
                                backStack.add(AppNavKey.EditTodo(id))
                            }
                        )
                    }
                    entry<AppNavKey.TodoCalendar> {
                        CalendarScreen()
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
}

@Composable
private fun FloatingToolbarItem(
    selected: Boolean,
    iconResourceId: Int,
    stringResourceId: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.secondaryContainer
    } else {
        Color.Transparent
    }

    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    TextButton(
        onClick = onClick,
        colors = ButtonDefaults.textButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        modifier = modifier
    ) {
        if (selected) {
            Icon(
                painter = painterResource(iconResourceId),
                contentDescription = stringResource(stringResourceId)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(text = stringResource(stringResourceId))
    }
}

private fun NavBackStack<NavKey>.navigateToTopLevel(destination: AppNavKey) {
    if (lastOrNull() == destination) return

    when (destination) {
        AppNavKey.TodoHome -> {
            while (size > 1) {
                removeLastOrNull()
            }
        }

        AppNavKey.TodoList,
        AppNavKey.TodoCalendar -> {
            removeAll { it == destination }
            add(destination)
        }

        else -> Unit
    }
}
