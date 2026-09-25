package com.example.todoapp.model

import androidx.annotation.StringRes
import com.example.todoapp.R

enum class TodoTag(@get:StringRes val labelRes: Int) {
    STUDY(R.string.todo_tag_study),
    WORK(R.string.todo_tag_work),
    HEALTH(R.string.todo_tag_health),
    HOBBY(R.string.todo_tag_hobby),
    SHOPPING(R.string.todo_tag_shopping)
}

enum class PlannedDateFilter(@get:StringRes val labelRes: Int) {
    ALL(R.string.todo_list_filter_all),
    TODAY(R.string.todo_list_filter_today),
    TOMORROW(R.string.todo_list_filter_tomorrow),
    THIS_WEEK(R.string.todo_list_filter_this_week),
    PAST_INCOMPLETE(R.string.todo_list_filter_past_incomplete),
    UNSCHEDULED(R.string.todo_list_filter_unscheduled)
}
