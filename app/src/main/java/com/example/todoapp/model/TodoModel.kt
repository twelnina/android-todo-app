package com.example.todoapp.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.example.todoapp.R

enum class TodoTag(@get:StringRes val labelRes: Int, val color: Color) {
    STUDY(R.string.study, Color(0xFFD1E4FF)),
    WORK(R.string.work, Color(0xFFB4F2BE)),
    HEALTH(R.string.health, Color(0xFFFFDAD6)),
    HOBBY(R.string.hobby, Color(0xFFF5D9FF)),
    SHOPPING(R.string.shopping, Color(0xFFFFF0AD))
}

enum class DueDateFilter(@get:StringRes val labelRes: Int) {
    ALL(R.string.all),
    TODAY(R.string.today),
    TOMORROW(R.string.tomorrow),
    THIS_WEEK(R.string.this_week),
    OVERDUE(R.string.overdue),
    NO_DATE(R.string.no_date)
}
