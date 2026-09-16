package com.example.todoapp.ui.components

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

@Composable
internal fun TagChip(tag: TodoTag, modifier: Modifier = Modifier) {
    Text(
        text = stringResource(tag.labelRes),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = tag.color,
        modifier = modifier
            .background(
                color = tag.color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = tag.color.copy(0.5f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}