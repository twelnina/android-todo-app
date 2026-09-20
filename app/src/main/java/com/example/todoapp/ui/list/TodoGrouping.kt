package com.example.todoapp.ui.list

import com.example.todoapp.data.local.TodoEntity

internal fun groupTodosByDate(todos: List<TodoEntity>): List<TodoDateGroup> {
    return todos
        .groupBy { todo -> todo.targetDate }
        .map { (date, todoForDate) ->
            TodoDateGroup(date, todoForDate)
        }
        .sortedWith { firstGroup, secondGroup ->
            when {
                firstGroup.date == null && secondGroup.date == null -> 0
                firstGroup.date == null -> 1
                secondGroup.date == null -> -1
                else -> firstGroup.date.compareTo(secondGroup.date)
            }
        }
}
