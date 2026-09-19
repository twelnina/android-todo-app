package com.example.todoapp.ui.home

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.R
import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.ui.home.components.CardItem
import com.example.todoapp.ui.home.components.TargetDatePickerDialog
import com.example.todoapp.ui.theme.RobotoFlexExpanded
import com.example.todoapp.ui.theme.TodoAppTheme
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.random.Random

@Composable
fun HomeScreen(
    onEditTodo: (Int) -> Unit, viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
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
    val reschedulingTodo =
        uiState.overdueItems.firstOrNull { it.todo.id == reschedulingTodoId }?.todo

    LaunchedEffect(hasItems) {
        if (hasItems && playInitialAnimation) {
            withFrameNanos { }
            playInitialAnimation = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                bottom = if (hasItems) 200.dp else 0.dp
            )
        ) {
            item(key = "home-header") {
                val locale = LocalConfiguration.current.locales[0]
                val formatter = remember(locale) {
                    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
                }
                val today = LocalDate.now()
                val now = LocalDateTime.now()
                Column(
                    modifier = Modifier.height(200.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = today.format(formatter),
                        fontFamily = RobotoFlexExpanded,
                        fontSize = 40.sp,
                        fontWeight = FontWeight(900),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = rememberHomeMessage(
                            hour = now.hour,
                            dayKey = now.toLocalDate().toEpochDay()
                        ),
                        fontFamily = RobotoFlexExpanded,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            item(key = "today-header") {
                Text(
                    text = stringResource(
                        R.string.today_section_title,
                        uiState.todayItems.size
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (uiState.todayItems.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.no_todos_today),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
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
            }

            item(key = "section-space") {
                Spacer(modifier = Modifier.height(32.dp))
            }

            item(key = "overdue-header") {
                Text(
                    text = stringResource(
                        R.string.overdue_section_title,
                        uiState.overdueItems.size
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (uiState.overdueItems.isEmpty()) {
                item {
                    Text(
                        stringResource(R.string.no_overdue_todos),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                itemsIndexed(
                    items = uiState.overdueItems,
                    key = { _, todo -> todo.todo.id }) { index, todo ->

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
}


@StringRes
private fun greetingResource(hour: Int): Int = when (hour) {
    in 5..11 -> R.string.greeting_morning
    in 12..17 -> R.string.greeting_afternoon
    else -> R.string.greeting_evening
}

@Composable
private fun rememberHomeMessage(
    hour: Int, dayKey: Long
): String {
    val greeting = stringResource(greetingResource(hour))
    val phrases = stringArrayResource(R.array.home_phrases)

    val phraseIndex = rememberSaveable(dayKey, phrases.size) {
        Random.nextInt(phrases.size)
    }

    return stringResource(
        R.string.home_message_format,
        greeting,
        phrases[phraseIndex]
    )
}


@Preview(showBackground = true)
@Composable
private fun EmptyHomeScreenPreview() {
    TodoAppTheme {
        HomeScreenContent(
            uiState = HomeUiState(),
            onCompletedChange = { _, _ -> },
            onRescheduleTodo = { _, _ -> },
            onEditTodo = {}
        )
    }
}
