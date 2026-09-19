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
    fun observeTodayItems_returnsOnlyTodosDueToday() = runBlocking {
        val today = LocalDate.of(2026, 9, 19)

        val dueToday = TodoEntity(
            id = 1,
            title = "Due today",
            description = "Should be returned",
            targetDate = today,
            tag = null
        )

        val dueTomorrow = TodoEntity(
            id = 2,
            title = "Due tomorrow",
            description = "Should not be returned",
            targetDate = today.plusDays(1),
            tag = null
        )

        todoDao.insert(dueToday)
        todoDao.insert(dueTomorrow)

        val actual = todoDao.observeTodayItems(today).first()

        assertEquals(listOf(dueToday), actual)
    }

    @Test
    fun observeOverdueItems_returnsOnlyIncompleteTodosBeforeToday() = runBlocking {
        val today = LocalDate.of(2026, 9, 19)

        val overdue = TodoEntity(
            id = 1,
            title = "Overdue",
            description = "Should be returned",
            targetDate = today.minusDays(1),
            tag = null
        )

        val dueToday = TodoEntity(
            id = 2,
            title = "Due today",
            description = "Should not be returned",
            targetDate = today,
            tag = null
        )

        val completedOverdue = TodoEntity(
            id = 3,
            title = "Completed overdue",
            description = "Should not be returned",
            targetDate = today.minusDays(2),
            tag = null,
            isCompleted = true
        )

        todoDao.insert(overdue)
        todoDao.insert(dueToday)
        todoDao.insert(completedOverdue)

        val actual = todoDao.observeOverdueItems(today).first()

        assertEquals(listOf(overdue), actual)
    }
}