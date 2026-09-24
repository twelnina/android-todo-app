package com.example.todoapp.ui.list.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.R
import com.example.todoapp.data.local.TodoEntity

@Composable
fun TodoItem(
    todoItemInfo: TodoEntity,
    index: Int,
    count: Int,
    expanded: Boolean,
    onCheckedChange: (TodoEntity, Boolean) -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quickActionsWidth = 176.dp

    val itemOffset by animateDpAsState(
        targetValue = if (expanded) -quickActionsWidth else 0.dp,
        label = "todoQuickActionsOffset"
    )

    val containerColor = if (todoItemInfo.isCompleted) {
        MaterialTheme.colorScheme.surfaceContainerLow
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }

    val contentColor = if (todoItemInfo.isCompleted) {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clipToBounds()
        ) {
            Box(modifier = Modifier.matchParentSize()) {
                TodoQuickActions(
                    enabled = expanded,
                    onChangePlannedDate = {},
                    onEdit = {},
                    onDelete = {},
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight()
                        .width(quickActionsWidth)
                )
            }
            SegmentedListItem(
                shapes = ListItemDefaults.segmentedShapes(index, count),
                colors = ListItemDefaults.segmentedColors(
                    containerColor = containerColor,
                    contentColor = contentColor,
                    leadingContentColor = contentColor,
                    supportingContentColor = contentColor
                ),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(itemOffset.roundToPx(), 0) },
                leadingContent = {
                    Checkbox(
                        checked = todoItemInfo.isCompleted,
                        onCheckedChange = { checked ->
                            onCheckedChange(todoItemInfo, checked)
                        }
                    )
                },
                trailingContent = {
                    IconButton(onClick = onMoreClick) {
                        Icon(
                            painter = painterResource(R.drawable.more_vert_24px),
                            contentDescription = null
                        )
                    }
                },
                supportingContent = {
                    Text(
                        text = todoItemInfo.description,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                },
            ) {
                Text(
                    text = todoItemInfo.title,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(ListItemDefaults.SegmentedGap))
    }
}

@Composable
private fun TodoQuickActions(
    enabled: Boolean,
    onChangePlannedDate: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            enabled = enabled,
            onClick = onDelete,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = 2.dp),
            shape = CircleShape,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.delete_24px),
                contentDescription = stringResource(R.string.delete)
            )
        }

        IconButton(
            enabled = enabled,
            onClick = onChangePlannedDate,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = 2.dp),
            shape = CircleShape,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.edit_calendar_24px),
                contentDescription = stringResource(R.string.change_planned_date)
            )
        }

        IconButton(
            enabled = enabled,
            onClick = onEdit,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(horizontal = 2.dp),
            shape = CircleShape,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.edit_24px),
                contentDescription = stringResource(R.string.edit_todo)
            )
        }
    }
}
