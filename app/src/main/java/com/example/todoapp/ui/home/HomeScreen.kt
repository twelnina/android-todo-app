package com.example.todoapp.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.R
import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.ui.home.components.CardItem
import com.example.todoapp.ui.home.components.TargetDatePickerDialog
import java.time.LocalDate

@Composable
fun HomeScreen(
    onEditTodo: (Int) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreenContent(
        uiState = uiState,
        onCompletedChange = viewModel::updateCompleted,
        onRescheduleTodo = viewModel::updateTargetDate,
        onEditTodo = onEditTodo
    )
}

@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onCompletedChange: (TodoEntity, Boolean) -> Unit,
    onRescheduleTodo: (TodoEntity, LocalDate) -> Unit,
    onEditTodo: (Int) -> Unit
) {
    var playInitialAnimation by rememberSaveable { mutableStateOf(true) }
    var reschedulingTodoId by rememberSaveable { mutableStateOf<Int?>(null) }

    val hasItems = uiState.todayItems.isNotEmpty() || uiState.overdueItems.isNotEmpty()
    val reschedulingTodo = uiState.overdueItems
        .firstOrNull { it.todo.id == reschedulingTodoId }
        ?.todo

    LaunchedEffect(hasItems) {
        if (hasItems && playInitialAnimation) {
            withFrameNanos { }
            playInitialAnimation = false
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, top = 200.dp, end = 16.dp, bottom = 200.dp)
    ) {
        item(key = "today-header") {
            Text(
                text = stringResource(R.string.today_section_title, uiState.todayItems.size),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        itemsIndexed(
            items = uiState.todayItems, key = { _, todo -> todo.id }) { index, todo ->

            val visibleState = remember {
                MutableTransitionState(!playInitialAnimation).apply {
                    targetState = true
                }
            }

            AnimatedVisibility(
                visibleState = visibleState, enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 300, delayMillis = index * 50
                    )
                ) + slideInVertically(
                    animationSpec = tween(
                        durationMillis = 300, delayMillis = index * 50
                    ), initialOffsetY = { height ->
                        height / 4
                    })
            ) {
                CardItem(
                    todo = todo,
                    onEdit = { onEditTodo(todo.id) },
                    onCheckedChange = { checked ->
                        onCompletedChange(todo, checked)
                    })
            }

            if (index < uiState.todayItems.lastIndex) {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }

        item(key = "section-space") {
            Spacer(modifier = Modifier.height(32.dp))
        }

        item(key = "overdue-header") {
            Text(
                text = stringResource(R.string.overdue_section_title, uiState.overdueItems.size),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        itemsIndexed(
            items = uiState.overdueItems, key = { _, todo -> todo.todo.id }) { index, todo ->

            val visibleState = remember {
                MutableTransitionState(!playInitialAnimation).apply {
                    targetState = true
                }
            }

            AnimatedVisibility(
                visibleState = visibleState, enter = fadeIn(
                    animationSpec = tween(
                        durationMillis = 300, delayMillis = index * 50
                    )
                ) + slideInVertically(
                    animationSpec = tween(
                        durationMillis = 300, delayMillis = index * 50
                    ),
                    initialOffsetY = { height ->
                        height / 4
                    })
            ) {
                CardItem(
                    todo = todo.todo,
                    onCheckedChange = { checked ->
                        onCompletedChange(todo.todo, checked)
                    },
                    onReschedule = { reschedulingTodoId = todo.todo.id },
                    onEdit = { onEditTodo(todo.todo.id) },
                    daysOverdue = todo.daysOverdue
                )
            }
            if (index < uiState.overdueItems.lastIndex) {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }

    reschedulingTodo?.let { todo ->
        TargetDatePickerDialog(
            previousDate = todo.targetDate,
            onDismissRequest = { reschedulingTodoId = null },
            onConfirmRequest = { newDate ->
                onRescheduleTodo(todo, newDate)
                reschedulingTodoId = null
            }
        )
    }
}
