package com.example.todoapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.todoapp.model.TodoTag
import com.example.todoapp.ui.theme.TodoAppTheme

@Composable
internal fun TagChip(tag: TodoTag, modifier: Modifier = Modifier) {
    val tagColor = TodoAppTheme.tagColors.colorFor(tag)

    Text(
        text = stringResource(tag.labelRes),
        fontSize = 10.sp,
        lineHeight = 10.sp,
        fontWeight = FontWeight.Bold,
        color = tagColor,
        modifier = modifier
            .background(
                color = tagColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = tagColor.copy(alpha = 0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
    )
}