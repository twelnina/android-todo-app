package com.example.todoapp.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.R
import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.model.TodoTag
import com.example.todoapp.ui.components.TagChip
import com.example.todoapp.ui.theme.TodoAppTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreenContent(
        uiState = uiState,
        onCompletedChange = viewModel::updateCompleted
    )
}

@Composable
private fun HomeScreenContent(
    uiState: HomeUiState,
    onCompletedChange: (TodoEntity, Boolean) -> Unit
) {
    var playInitialAnimation by rememberSaveable { mutableStateOf(true) }

    val hasItems = uiState.todayItems.isNotEmpty() || uiState.overdueItems.isNotEmpty()

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
            items = uiState.todayItems,
            key = { _, todo -> todo.id }
        ) { index, todo ->

            val visibleState = remember {
                MutableTransitionState(!playInitialAnimation).apply {
                    targetState = true
                }
            }

            AnimatedVisibility(
                visibleState = visibleState,
                enter =
                    fadeIn(
                        animationSpec = tween(
                            durationMillis = 300,
                            delayMillis = index * 50
                        )
                    ) + slideInVertically(
                        animationSpec = tween(
                            durationMillis = 300,
                            delayMillis = index * 50
                        ),
                        initialOffsetY = { height ->
                            height / 4
                        }
                    )
            ) {
                CardItem(
                    title = todo.title,
                    description = todo.description,
                    tag = todo.tag,
                    isCompleted = todo.isCompleted,
                    onCheckedChange = { checked ->
                        onCompletedChange(todo, checked)
                    }
                )
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
            items = uiState.overdueItems,
            key = { _, todo -> todo.todo.id }
        ) { index, todo ->

            val visibleState = remember {
                MutableTransitionState(!playInitialAnimation).apply {
                    targetState = true
                }
            }

            AnimatedVisibility(
                visibleState = visibleState,
                enter =
                    fadeIn(
                        animationSpec = tween(
                            durationMillis = 300,
                            delayMillis = index * 50
                        )
                    ) + slideInVertically(
                        animationSpec = tween(
                            durationMillis = 300,
                            delayMillis = index * 50
                        ),
                        initialOffsetY = { height ->
                            height / 4
                        }
                    )
            ) {
                CardItem(
                    title = todo.todo.title,
                    description = todo.todo.description,
                    tag = todo.todo.tag,
                    isCompleted = todo.todo.isCompleted,
                    onCheckedChange = { checked ->
                        onCompletedChange(todo.todo, checked)
                    },
                    daysOverdue = todo.daysOverdue
                )
            }
            if (index < uiState.overdueItems.lastIndex) {
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun CardItem(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    tag: TodoTag?,
    isCompleted: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    daysOverdue: Long? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh
            },
            contentColor = if (isCompleted) {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = onCheckedChange,
            )
            Column(modifier = Modifier.weight(1f)) {
                daysOverdue?.let {
                    val days = it.toInt()

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.schedule_24px),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = pluralStringResource(R.plurals.days_overdue, days, days),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            tag?.let {
                TagChip(tag = tag, modifier = Modifier.padding(horizontal = 8.dp))
            }
        }
    }
}


@Preview
@Composable
private fun CardItemPreview() {
    TodoAppTheme {
        CardItem(
            title = "Review English vocabulary",
            description = "Review this week's vocabulary list, practice each word in a sentence. and flag difficult terms for another focused study session",
            tag = TodoTag.STUDY,
            onCheckedChange = {},
            isCompleted = false
        )
    }
}