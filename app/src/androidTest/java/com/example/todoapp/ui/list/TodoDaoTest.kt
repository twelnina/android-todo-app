package com.example.todoapp.ui.list

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.todoapp.data.local.AppDatabase
import com.example.todoapp.data.local.TodoDao
import com.example.todoapp.data.local.TodoEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class TodoDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var todoDao: TodoDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()

        todoDao = database.todoDao()
    }

    @After
    fun tearDown() {
        database.close()
    }


    @Test
    fun observeTodayItems_returnsOnlyTodosPlannedToday() = runBlocking {
        val today = LocalDate.of(2026, 9, 19)

        val plannedToday = TodoEntity(
            id = 1,
            title = "Planned for today",
            description = "Should be returned",
            plannedDate = today,
            tag = null
        )

        val plannedTomorrow = TodoEntity(
            id = 2,
            title = "Planned for tomorrow",
            description = "Should not be returned",
            plannedDate = today.plusDays(1),
            tag = null
        )

        todoDao.insert(plannedToday)
        todoDao.insert(plannedTomorrow)

        val actual = todoDao.observeTodayItems(today).first()

        assertEquals(listOf(plannedToday), actual)
    }

    @Test
    fun observePastIncompleteItems_returnsOnlyIncompleteTodosBeforeToday() = runBlocking {
        val today = LocalDate.of(2026, 9, 19)

        val pastIncomplete = TodoEntity(
            id = 1,
            title = "Earlier task",
            description = "Should be returned",
            plannedDate = today.minusDays(1),
            tag = null
        )

        val plannedToday = TodoEntity(
            id = 2,
            title = "Planned for today",
            description = "Should not be returned",
            plannedDate = today,
            tag = null
        )

        val completedEarlier = TodoEntity(
            id = 3,
            title = "Completed earlier task",
            description = "Should not be returned",
            plannedDate = today.minusDays(2),
            tag = null,
            isCompleted = true
        )

        todoDao.insert(pastIncomplete)
        todoDao.insert(plannedToday)
        todoDao.insert(completedEarlier)

        val actual = todoDao.observePastIncompleteItems(today).first()

        assertEquals(listOf(pastIncomplete), actual)
    }
}