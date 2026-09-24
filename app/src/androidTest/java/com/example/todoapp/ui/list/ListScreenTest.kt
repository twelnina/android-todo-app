package com.example.todoapp.ui.list

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.model.PlannedDateFilter
import com.example.todoapp.model.TodoTag
import com.example.todoapp.ui.theme.TodoAppTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class ListScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysTodoFromUiState() {
        val todo = TodoEntity(
            id = 1,
            title = "Study Kotlin",
            description = "Read the documentation",
            plannedDate = LocalDate.of(2026, 9, 13),
            tag = TodoTag.STUDY
        )

        val uiState = ListUiState(
            todoGroups = listOf(
                TodoDateGroup(
                    date = todo.plannedDate,
                    todos = listOf(todo)
                )
            )
        )

        composeTestRule.setContent {
            TodoAppTheme {
                ListScreenContent(
                    uiState = uiState,
                    onCheckedChange = { _, _ -> },
                    onQueryChange = {},
                    onTagSelected = {},
                    onPlannedDateChipClick = {},
                    onPlannedDateFilterChange = {},
                    onDismissRequest = {},
                    onEdit = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Study Kotlin")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Sep 13")
            .assertIsDisplayed()
    }

    @Test
    fun enteringSearchQueryCallsOnQueryChange() {
        val query = mutableStateOf("")

        composeTestRule.setContent {
            TodoAppTheme {
                ListScreenContent(
                    uiState = ListUiState(searchQuery = query.value),
                    onCheckedChange = { _, _ -> },
                    onQueryChange = { newQuery ->
                        query.value = newQuery
                    },
                    onTagSelected = {},
                    onPlannedDateChipClick = {},
                    onPlannedDateFilterChange = {},
                    onDismissRequest = {},
                    onEdit = {}
                )
            }
        }

        composeTestRule
            .onNode(hasSetTextAction())
            .performTextInput("Kotlin")

        composeTestRule.runOnIdle {
            assertEquals("Kotlin", query.value)
        }
    }

    @Test
    fun tappingTagCallsOnTagSelected() {
        var selectedTag: TodoTag? = null

        composeTestRule.setContent {
            TodoAppTheme {
                ListScreenContent(
                    uiState = ListUiState(),
                    onCheckedChange = { _, _ -> },
                    onQueryChange = {},
                    onTagSelected = { tag ->
                        selectedTag = tag
                    },
                    onPlannedDateChipClick = {},
                    onPlannedDateFilterChange = {},
                    onDismissRequest = {},
                    onEdit = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Work")
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(TodoTag.WORK, selectedTag)
        }
    }

    @Test
    fun selectingPlannedDateCallsOnPlannedDateFilterChange() {
        val showBottomSheet = mutableStateOf(false)
        var selectedFilter: PlannedDateFilter? = null

        composeTestRule.setContent {
            TodoAppTheme {
                ListScreenContent(
                    uiState = ListUiState(showBottomSheet = showBottomSheet.value),
                    onCheckedChange = { _, _ -> },
                    onQueryChange = {},
                    onTagSelected = {},
                    onPlannedDateChipClick = {
                        showBottomSheet.value = true
                    },
                    onPlannedDateFilterChange = { filter ->
                        selectedFilter = filter
                        showBottomSheet.value = false
                    },
                    onDismissRequest = { showBottomSheet.value = false },
                    onEdit = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Planned date")
            .performClick()

        composeTestRule
            .onNodeWithText("Today")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                PlannedDateFilter.TODAY,
                selectedFilter
            )
        }
    }

    @Test
    fun displaysUnscheduledHeaderForTodoWithoutPlannedDate() {
        val todo = TodoEntity(
            id = 2,
            title = "Read a book",
            description = "Read one chapter",
            plannedDate = null,
            tag = null
        )

        val uiState = ListUiState(
            todoGroups = listOf(TodoDateGroup(null, listOf(todo)))
        )

        composeTestRule.setContent {
            TodoAppTheme {
                ListScreenContent(
                    uiState = uiState,
                    onCheckedChange = { _, _ -> },
                    onQueryChange = {},
                    onTagSelected = {},
                    onPlannedDateChipClick = {},
                    onPlannedDateFilterChange = {},
                    onDismissRequest = {},
                    onEdit = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Unscheduled")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Read a book")
            .assertIsDisplayed()
    }

    @Test
    fun tappingTodoCallsOnEditTodoWithTodoId() {
        val todo = TodoEntity(
            id = 42,
            title = "Update project",
            description = "Review the implementation",
            plannedDate = null,
            tag = null
        )

        val uiState = ListUiState(
            todoGroups = listOf(
                TodoDateGroup(todo.plannedDate, listOf(todo))
            )
        )

        var editedTodoId: Int? = null

        composeTestRule.setContent {
            TodoAppTheme {
                ListScreenContent(
                    uiState = uiState,
                    onCheckedChange = { _, _ -> },
                    onQueryChange = {},
                    onTagSelected = {},
                    onPlannedDateChipClick = {},
                    onPlannedDateFilterChange = {},
                    onDismissRequest = {},
                    onEdit = { todoId ->
                        editedTodoId = todoId
                    }
                )
            }
        }

        composeTestRule
            .onNodeWithText("Update project")
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(42, editedTodoId)
        }
    }
}
