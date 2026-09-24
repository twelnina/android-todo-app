package com.example.todoapp.ui.list.component

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasNoClickAction
import androidx.compose.ui.test.isOff
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.example.todoapp.R
import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.ui.theme.TodoAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class TodoItemTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val todo = TodoEntity(
        id = 1,
        title = "Study Kotlin",
        description = "Read Compose documentation",
        plannedDate = null,
        tag = null
    )

    @Test
    fun todoContent_hasNoClickAction() {
        setTodoItem()

        composeTestRule
            .onNodeWithText(todo.title)
            .assert(hasNoClickAction())

        composeTestRule
            .onNodeWithText(todo.description)
            .assert(hasNoClickAction())
    }

    @Test
    fun checkbox_callsOnCheckedChangeWithTodo() {
        var updatedTodo: TodoEntity? = null
        var updatedValue: Boolean? = null

        setTodoItem(
            onCheckedChange = { selectedTodo, checked ->
                updatedTodo = selectedTodo
                updatedValue = checked
            }
        )

        composeTestRule
            .onNode(isToggleable() and isOff())
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(todo, updatedTodo)
            assertEquals(true, updatedValue)
        }
    }

    @Test
    fun quickActions_areEnabledOnlyWhileExpanded() {
        var expanded by mutableStateOf(false)

        setTodoItem(
            expandedProvider = { expanded },
            onMoreClick = { expanded = !expanded }
        )

        val editDescription = getString(R.string.edit_todo)

        composeTestRule
            .onNodeWithContentDescription(editDescription)
            .assertIsNotEnabled()

        composeTestRule
            .onNodeWithContentDescription(getString(R.string.more_options))
            .performClick()

        composeTestRule
            .onNodeWithContentDescription(editDescription)
            .assertIsEnabled()
    }

    @Test
    fun quickActions_callCorrespondingCallbacks() {
        val calledActions = mutableListOf<String>()

        setTodoItem(
            expandedProvider = { true },
            onChangePlannedDate = { calledActions += "date" },
            onEdit = { calledActions += "edit" },
            onDelete = { calledActions += "delete" }
        )

        composeTestRule
            .onNodeWithContentDescription(getString(R.string.change_planned_date))
            .performClick()

        composeTestRule
            .onNodeWithContentDescription(getString(R.string.edit_todo))
            .performClick()

        composeTestRule
            .onNodeWithContentDescription(getString(R.string.delete))
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                listOf("date", "edit", "delete"),
                calledActions
            )
        }
    }

    private fun setTodoItem(
        expandedProvider: () -> Boolean = { false },
        onCheckedChange: (TodoEntity, Boolean) -> Unit = { _, _ -> },
        onChangePlannedDate: () -> Unit = {},
        onMoreClick: () -> Unit = {},
        onEdit: () -> Unit = {},
        onDelete: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            TodoAppTheme {
                TodoItem(
                    todoItemInfo = todo,
                    index = 0,
                    count = 1,
                    expanded = expandedProvider(),
                    onCheckedChange = onCheckedChange,
                    onMoreClick = onMoreClick,
                    onChangePlannedDate = onChangePlannedDate,
                    onEdit = onEdit,
                    onDelete = onDelete
                )
            }
        }
    }

    private fun getString(@StringRes resourceId: Int): String {
        return InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .getString(resourceId)
    }
}
