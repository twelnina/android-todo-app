package com.example.todoapp.ui.list

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.model.DueDateFilter
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
            targetDate = LocalDate.of(2026, 9, 13),
            tag = TodoTag.STUDY
        )

        val uiState = ListUiState(todoEntities = listOf(todo))

        composeTestRule.setContent {
            TodoAppTheme {
                ListScreenContent(
                    uiState = uiState,
                    onQueryChange = {},
                    onTagSelected = {},
                    onDueDateChipClick = {},
                    onDueDateFilterChange = {},
                    onDismissRequest = {},
                    onEditTodo = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Study Kotlin")
            .assertIsDisplayed()
    }

    @Test
    fun enteringSearchQueryCallsOnQueryChange() {
        val query = mutableStateOf("")

        composeTestRule.setContent {
            TodoAppTheme {
                ListScreenContent(
                    uiState = ListUiState(searchQuery = query.value),
                    onQueryChange = { newQuery ->
                        query.value = newQuery
                    },
                    onTagSelected = {},
                    onDueDateChipClick = {},
                    onDueDateFilterChange = {},
                    onDismissRequest = {},
                    onEditTodo = {}
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
                    onQueryChange = {},
                    onTagSelected = { tag ->
                        selectedTag = tag
                    },
                    onDueDateChipClick = {},
                    onDueDateFilterChange = {},
                    onDismissRequest = {},
                    onEditTodo = {}
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
    fun selectingDueDateCallsOnDueDateFilterChange() {
        val showBottomSheet = mutableStateOf(false)
        var selectedFilter: DueDateFilter? = null

        composeTestRule.setContent {
            TodoAppTheme {
                ListScreenContent(
                    uiState = ListUiState(showBottomSheet = showBottomSheet.value),
                    onQueryChange = {},
                    onTagSelected = {},
                    onDueDateChipClick = {
                        showBottomSheet.value = true
                    },
                    onDueDateFilterChange = { filter ->
                        selectedFilter = filter
                        showBottomSheet.value = false
                    },
                    onDismissRequest = { showBottomSheet.value = false },
                    onEditTodo = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Due date")
            .performClick()

        composeTestRule
            .onNodeWithText("Today")
            .assertIsDisplayed()
            .performClick()

        composeTestRule.runOnIdle {
            assertEquals(
                DueDateFilter.TODAY,
                selectedFilter
            )
        }
    }
}