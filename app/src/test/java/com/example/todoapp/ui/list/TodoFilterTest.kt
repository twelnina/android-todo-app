package com.example.todoapp.ui.list

import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.model.DueDateFilter
import com.example.todoapp.model.TodoTag
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class TodoFilterTest {
    @Test
    fun `query matches title ignoring case`() {
        val today = LocalDate.of(2026, 9, 12)

        val kotlinTodo = TodoEntity(
            id = 1,
            title = "Study Kotlin",
            description = "Read the documentation",
            targetDate = today,
            tag = TodoTag.STUDY
        )

        val shoppingTodo = TodoEntity(
            id = 2,
            title = "Buy milk",
            description = "Go to the supermarket",
            targetDate = today,
            tag = TodoTag.SHOPPING
        )

        val result = filterTodos(
            items = listOf(kotlinTodo, shoppingTodo),
            query = "KOTLIN",
            tags = emptySet(),
            dueDate = DueDateFilter.ALL,
            today = today
        )

        assertEquals(listOf(kotlinTodo), result)
    }

    @Test
    fun `query matches description ignoring case`() {
        val today = LocalDate.of(2026, 9, 12)

        val kotlinTodo = TodoEntity(
            id = 1,
            title = "Study Programming",
            description = "Read the kotlin documentation",
            targetDate = today,
            tag = TodoTag.STUDY
        )

        val shoppingTodo = TodoEntity(
            id = 2,
            title = "Buy milk",
            description = "Go to the supermarket",
            targetDate = today,
            tag = TodoTag.SHOPPING
        )

        val result = filterTodos(
            items = listOf(kotlinTodo, shoppingTodo),
            query = "DOCUMENTATION",
            tags = emptySet(),
            dueDate = DueDateFilter.ALL,
            today = today
        )

        assertEquals(listOf(kotlinTodo), result)
    }

    @Test
    fun `selected tag returns matching todos`() {
        val today = LocalDate.of(2026, 9, 12)

        val workTodo = TodoEntity(
            id = 1,
            title = "Write report",
            description = "Prepare the monthly report",
            targetDate = today,
            tag = TodoTag.WORK
        )

        val studyTodo = TodoEntity(
            id = 2,
            title = "Study Kotlin",
            description = "Read the documentation",
            targetDate = today,
            tag = TodoTag.STUDY
        )

        val noTagTodo = TodoEntity(
            id = 3,
            title = "Clean room",
            description = "Organize the desk",
            targetDate = today,
            tag = null
        )

        val result = filterTodos(
            items = listOf(workTodo, studyTodo, noTagTodo),
            query = "",
            tags = setOf(TodoTag.WORK),
            dueDate = DueDateFilter.ALL,
            today = today
        )

        assertEquals(listOf(workTodo), result)
    }

    @Test
    fun `multiple selected tags return todos matching any tag`() {
        val today = LocalDate.of(2026, 9, 13)

        val workTodo = TodoEntity(
            id = 1,
            title = "Write report",
            description = "Prepare the monthly report",
            targetDate = today,
            tag = TodoTag.WORK
        )

        val studyTodo = TodoEntity(
            id = 2,
            title = "Study Kotlin",
            description = "Read the documentation",
            targetDate = today,
            tag = TodoTag.STUDY
        )

        val shoppingTodo = TodoEntity(
            id = 3,
            title = "Buy milk",
            description = "Go to the supermarket",
            targetDate = today,
            tag = TodoTag.SHOPPING
        )

        val result = filterTodos(
            items = listOf(workTodo, studyTodo, shoppingTodo),
            query = "",
            tags = setOf(TodoTag.WORK, TodoTag.STUDY),
            dueDate = DueDateFilter.ALL,
            today = today
        )

        assertEquals(listOf(workTodo, studyTodo), result)
    }

    @Test
    fun `today filter returns only todos due today`() {
        val today = LocalDate.of(2026, 9, 9)

        val yesterdayTodo = TodoEntity(
            id = 1,
            title = "Yesterday task",
            description = "This task was due yesterday",
            targetDate = today.minusDays(1),
            tag = TodoTag.WORK
        )

        val todayTodo = TodoEntity(
            id = 2,
            title = "Today task",
            description = "This task is due today",
            targetDate = today,
            tag = TodoTag.STUDY
        )

        val tomorrowTodo = TodoEntity(
            id = 3,
            title = "Tomorrow task",
            description = "This task is due tomorrow",
            targetDate = today.plusDays(1),
            tag = TodoTag.SHOPPING
        )

        val noDateTodo = TodoEntity(
            id = 4,
            title = "No date task",
            description = "This task has no due date",
            targetDate = null,
            tag = null
        )

        val result = filterTodos(
            items = listOf(
                yesterdayTodo,
                todayTodo,
                tomorrowTodo,
                noDateTodo
            ),
            query = "",
            tags = emptySet(),
            dueDate = DueDateFilter.TODAY,
            today = today
        )

        assertEquals(listOf(todayTodo), result)
    }

    @Test
    fun `tomorrow filter returns only todos due tomorrow`() {
        val today = LocalDate.of(2026, 9, 9)

        val todayTodo = TodoEntity(
            id = 1,
            title = "Today task",
            description = "This task is due today",
            targetDate = today,
            tag = TodoTag.WORK
        )

        val tomorrowTodo = TodoEntity(
            id = 2,
            title = "Tomorrow task",
            description = "This task is due tomorrow",
            targetDate = today.plusDays(1),
            tag = TodoTag.STUDY
        )

        val dayAfterTomorrowTodo = TodoEntity(
            id = 3,
            title = "Day after tomorrow task",
            description = "This task is due in two days",
            targetDate = today.plusDays(2),
            tag = TodoTag.SHOPPING
        )

        val noDateTodo = TodoEntity(
            id = 4,
            title = "No date task",
            description = "This task has no due date",
            targetDate = null,
            tag = null
        )

        val result = filterTodos(
            items = listOf(
                todayTodo,
                tomorrowTodo,
                dayAfterTomorrowTodo,
                noDateTodo
            ),
            query = "",
            tags = emptySet(),
            dueDate = DueDateFilter.TOMORROW,
            today = today
        )

        assertEquals(listOf(tomorrowTodo), result)
    }

    @Test
    fun `this week filter returns todos from today through Sunday`() {
        // It's Wednesday
        val today = LocalDate.of(2026, 9, 9)

        val yesterdayTodo = TodoEntity(
            id = 1,
            title = "Yesterday task",
            description = "This task was due yesterday",
            targetDate = today.minusDays(1),
            tag = null
        )

        val todayTodo = TodoEntity(
            id = 2,
            title = "Today task",
            description = "This task is due today",
            targetDate = today,
            tag = null
        )

        val fridayTodo = TodoEntity(
            id = 3,
            title = "Friday task",
            description = "This task is due on Friday",
            targetDate = today.plusDays(2),
            tag = null
        )

        val sundayTodo = TodoEntity(
            id = 4,
            title = "Sunday task",
            description = "This task is due on Sunday",
            targetDate = today.plusDays(4),
            tag = null
        )

        val nextMondayTodo = TodoEntity(
            id = 5,
            title = "Next Monday task",
            description = "This task is due next week",
            targetDate = today.plusDays(5),
            tag = null
        )

        val noDateTodo = TodoEntity(
            id = 6,
            title = "No date task",
            description = "This task has no due date",
            targetDate = null,
            tag = null
        )

        val result = filterTodos(
            items = listOf(
                yesterdayTodo,
                todayTodo,
                fridayTodo,
                sundayTodo,
                nextMondayTodo,
                noDateTodo
            ),
            query = "",
            tags = emptySet(),
            dueDate = DueDateFilter.THIS_WEEK,
            today = today
        )

        assertEquals(listOf(todayTodo, fridayTodo, sundayTodo), result)
    }

    @Test
    fun `overdue filter returns only todos before today`() {
        val today = LocalDate.of(2026, 9, 9)

        val lastWeekTodo = TodoEntity(
            id = 1,
            title = "Last week task",
            description = "This task was due last week",
            targetDate = today.minusDays(7),
            tag = null
        )

        val yesterdayTodo = TodoEntity(
            id = 2,
            title = "Yesterday task",
            description = "This task was due yesterday",
            targetDate = today.minusDays(1),
            tag = null
        )

        val todayTodo = TodoEntity(
            id = 3,
            title = "Today task",
            description = "This task is due today",
            targetDate = today,
            tag = null
        )

        val tomorrowTodo = TodoEntity(
            id = 4,
            title = "Tomorrow task",
            description = "This task is due tomorrow",
            targetDate = today.plusDays(1),
            tag = null
        )

        val noDateTodo = TodoEntity(
            id = 5,
            title = "No date task",
            description = "This task has no due date",
            targetDate = null,
            tag = null
        )

        val result = filterTodos(
            items = listOf(
                lastWeekTodo,
                yesterdayTodo,
                todayTodo,
                tomorrowTodo,
                noDateTodo
            ),
            query = "",
            tags = emptySet(),
            dueDate = DueDateFilter.OVERDUE,
            today = today
        )

        assertEquals(listOf(lastWeekTodo, yesterdayTodo), result)
    }

    @Test
    fun `no date filter returns only todos without due date`() {
        val today = LocalDate.of(2026, 9, 9)

        val overdueTodo = TodoEntity(
            id = 1,
            title = "Overdue task",
            description = "This task was due yesterday",
            targetDate = today.minusDays(1),
            tag = null
        )

        val todayTodo = TodoEntity(
            id = 2,
            title = "Today task",
            description = "This task is due today",
            targetDate = today,
            tag = null
        )

        val futureTodo = TodoEntity(
            id = 3,
            title = "Future task",
            description = "This task is due next week",
            targetDate = today.plusDays(7),
            tag = null
        )

        val noDateTodo = TodoEntity(
            id = 4,
            title = "No date task",
            description = "This task has no due date",
            targetDate = null,
            tag = null
        )

        val result = filterTodos(
            items = listOf(
                overdueTodo,
                todayTodo,
                futureTodo,
                noDateTodo
            ),
            query = "",
            tags = emptySet(),
            dueDate = DueDateFilter.NO_DATE,
            today = today
        )

        assertEquals(listOf(noDateTodo), result)
    }

    @Test
    fun `all date filter returns dated and undated todos`() {
        val today = LocalDate.of(2026, 9, 9)

        val overdueTodo = TodoEntity(
            id = 1,
            title = "Overdue task",
            description = "This task was due yesterday",
            targetDate = today.minusDays(1),
            tag = null
        )

        val todayTodo = TodoEntity(
            id = 2,
            title = "Today task",
            description = "This task is due today",
            targetDate = today,
            tag = null
        )

        val futureTodo = TodoEntity(
            id = 3,
            title = "Future task",
            description = "This task is due next week",
            targetDate = today.plusDays(7),
            tag = null
        )

        val noDateTodo = TodoEntity(
            id = 4,
            title = "No date task",
            description = "This task has no due date",
            targetDate = null,
            tag = null
        )

        val result = filterTodos(
            items = listOf(
                overdueTodo,
                todayTodo,
                futureTodo,
                noDateTodo
            ),
            query = "",
            tags = emptySet(),
            dueDate = DueDateFilter.ALL,
            today = today
        )

        assertEquals(
            listOf(
                overdueTodo,
                todayTodo,
                futureTodo,
                noDateTodo
            ),
            result
        )
    }

    @Test
    fun `combined filters return todos matching every condition`() {
        val today = LocalDate.of(2026, 9, 9)

        val matchingTodo = TodoEntity(
            id = 1,
            title = "Write report",
            description = "Prepare the monthly summary",
            targetDate = today,
            tag = TodoTag.WORK
        )

        val queryMismatchTodo = TodoEntity(
            id = 2,
            title = "Prepare slides",
            description = "Create the presentation",
            targetDate = today,
            tag = TodoTag.WORK
        )

        val tagMismatchTodo = TodoEntity(
            id = 3,
            title = "Study report",
            description = "Read the study materials",
            targetDate = today,
            tag = TodoTag.STUDY
        )

        val dateMismatchTodo = TodoEntity(
            id = 4,
            title = "Future report",
            description = "Prepare next month's summary",
            targetDate = today.plusDays(1),
            tag = TodoTag.WORK
        )

        val result = filterTodos(
            items = listOf(
                matchingTodo,
                queryMismatchTodo,
                tagMismatchTodo,
                dateMismatchTodo
            ),
            query = "report",
            tags = setOf(TodoTag.WORK),
            dueDate = DueDateFilter.TODAY,
            today = today
        )

        assertEquals(listOf(matchingTodo), result)
    }
}
