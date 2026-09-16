package com.example.todoapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.R
import com.example.todoapp.model.TodoTag
import com.example.todoapp.ui.components.TagChip
import com.example.todoapp.ui.theme.TodoAppTheme

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    HomeScreenContent(uiState = uiState)
}

@Composable
private fun HomeScreenContent(
    uiState: HomeUiState
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        TodoSection(
            title = stringResource(R.string.today_section_title)
        ) {
            uiState.todayItems.forEach { items ->
                CardItem(
                    title = items.title,
                    description = items.description,
                    tag = items.tag,
                    isCompleted = false
                )
            }
        }
        TodoSection(
            title = stringResource(R.string.overdue_section_title)
        ) {
            uiState.overdueItems.forEach { items ->
                CardItem(
                    title = items.title,
                    description = items.description,
                    tag = items.tag,
                    isCompleted = false
                )
            }
        }
    }
}

@Composable
private fun TodoSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            content = content
        )
    }
}

@Composable
private fun CardItem(
    title: String,
    description: String,
    tag: TodoTag?,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f)
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh
            },
            contentColor = if (isCompleted) {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
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
                onCheckedChange = { },
            )
            Column(modifier = Modifier.weight(1f)) {
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
            isCompleted = false
        )
    }
}