package com.example.todoapp.model

import androidx.compose.ui.graphics.Color
import java.time.LocalDate

enum class TodoTag(val color: Color) {
    STUDY(Color(0xFFD1E4FF)),
    WORK(Color(0xFFB4F2BE)),
    HEALTH(Color(0xFFFFDAD6)),
    HOBBY(Color(0xFFF5D9FF)),
    SHOPPING(Color(0xFFFFF0AD))
}

data class TodoModel(
    val id: Int,
    val title: String,
    val detail: String,
    val date: LocalDate,
    val label: TodoTag
)
