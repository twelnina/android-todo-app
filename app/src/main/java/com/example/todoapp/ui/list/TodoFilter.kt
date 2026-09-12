package com.example.todoapp.ui.list

import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.model.DueDateFilter
import com.example.todoapp.model.TodoTag
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

internal fun filterTodos(
    items: List<TodoEntity>,
    query: String,
    tags: Set<TodoTag>,
    dueDate: DueDateFilter,
    today: LocalDate = LocalDate.now()
): List<TodoEntity> {
    return items.filter { item ->
        val matchesQuery =
            item.title.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true)
        val matchesTag = if (tags.isEmpty()) {
            true
        } else {
            item.tag in tags
        }
        val matchesDate = when (dueDate) {
            DueDateFilter.ALL -> true
            DueDateFilter.TODAY -> item.targetDate == today
            DueDateFilter.TOMORROW -> item.targetDate == today.plusDays(1)
            DueDateFilter.THIS_WEEK -> {
                val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                item.targetDate?.let { date ->
                    (date.isEqual(today) || date.isAfter(today)) &&
                            (date.isEqual(endOfWeek) || date.isBefore(endOfWeek))
                } ?: false
            }

            DueDateFilter.OVERDUE -> item.targetDate?.isBefore(today) ?: false
            DueDateFilter.NO_DATE -> item.targetDate == null
        }

        matchesQuery && matchesTag && matchesDate
    }
}