package com.example.todoapp.ui.list

import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.model.PlannedDateFilter
import com.example.todoapp.model.TodoTag
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

internal fun filterTodos(
    items: List<TodoEntity>,
    query: String,
    tags: Set<TodoTag>,
    plannedDateFilter: PlannedDateFilter,
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
        val matchesDate = when (plannedDateFilter) {
            PlannedDateFilter.ALL -> true
            PlannedDateFilter.TODAY -> item.plannedDate == today
            PlannedDateFilter.TOMORROW -> item.plannedDate == today.plusDays(1)
            PlannedDateFilter.THIS_WEEK -> {
                val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                item.plannedDate?.let { date ->
                    (date.isEqual(today) || date.isAfter(today)) &&
                            (date.isEqual(endOfWeek) || date.isBefore(endOfWeek))
                } ?: false
            }

            PlannedDateFilter.PAST_INCOMPLETE -> !item.isCompleted &&
                    (item.plannedDate?.isBefore(today) ?: false)

            PlannedDateFilter.UNSCHEDULED -> item.plannedDate == null
        }

        matchesQuery && matchesTag && matchesDate
    }
}