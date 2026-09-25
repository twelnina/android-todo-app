package com.example.todoapp.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.example.todoapp.R

enum class TodoTag(@get:StringRes val labelRes: Int, val color: Color) {
    STUDY(R.string.todo_tag_study, Color(0xFFD1E4FF)),
    WORK(R.string.todo_tag_work, Color(0xFFB4F2BE)),
    HEALTH(R.string.todo_tag_health, Color(0xFFFFDAD6)),
    HOBBY(R.string.todo_tag_hobby, Color(0xFFF5D9FF)),
    SHOPPING(R.string.todo_tag_shopping, Color(0xFFFFF0AD))
}

enum class PlannedDateFilter(@get:StringRes val labelRes: Int) {
    ALL(R.string.todo_list_filter_all),
    TODAY(R.string.todo_list_filter_today),
    TOMORROW(R.string.todo_list_filter_tomorrow),
    THIS_WEEK(R.string.todo_list_filter_this_week),
    PAST_INCOMPLETE(R.string.todo_list_filter_past_incomplete),
    UNSCHEDULED(R.string.todo_list_filter_unscheduled)
}
