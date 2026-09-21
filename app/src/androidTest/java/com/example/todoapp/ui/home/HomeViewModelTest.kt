package com.example.todoapp.ui.home

import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.todoapp.data.local.AppDatabase
import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.data.repository.TodoRepository
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import kotlin.time.Duration.Companion.milliseconds

class HomeViewModelTest {
    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDatabase::class.java
        ).build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun uiState_updatesItemAndDaysOverdueWhenDateChanges() = runBlocking {
        val firstDate = LocalDate.of(2026, 9, 21)
        val nextDate = firstDate.plusDays(1)

        val alreadyOverdue = createTodo(1, firstDate.minusDays(1))
        val dueToday = createTodo(2, firstDate)
        val dueTomorrow = createTodo(3, nextDate)
        val completedOverdue = createTodo(4, firstDate.minusDays(1), true)

        val dao = database.todoDao()
        listOf(alreadyOverdue, dueToday, dueTomorrow, completedOverdue).forEach { dao.insert(it) }

        val dateProvider = FakeCurrentDateProvider(firstDate)
        val viewModel = HomeViewModel(
            repository = TodoRepository(dao), currentDateProvider = dateProvider
        )

        val collector = launch(start = CoroutineStart.UNDISPATCHED) {
            viewModel.uiState.collect { }
        }

        try {
            withTimeout(5_000.milliseconds) {
                val before = viewModel.uiState.first {
                    it.today == firstDate
                }
                assertEquals(listOf(dueToday), before.todayItems)
                assertEquals(
                    listOf(
                        OverdueItem(todo = alreadyOverdue, daysOverdue = 1L)
                    ),
                    before.overdueItems
                )

                dateProvider.changeDate(nextDate)

                val after = viewModel.uiState.first {
                    it.today == nextDate
                }
                assertEquals(listOf(dueTomorrow), after.todayItems)
                assertEquals(
                    listOf(
                        OverdueItem(todo = alreadyOverdue, daysOverdue = 2L),
                        OverdueItem(todo = dueToday, daysOverdue = 1L)
                    ),
                    after.overdueItems
                )
            }
        } finally {
            collector.cancelAndJoin()
            viewModel.viewModelScope.coroutineContext.job.cancelAndJoin()
        }
    }

    private fun createTodo(id: Int, targetDate: LocalDate, isCompleted: Boolean = false) =
        TodoEntity(
            id = id,
            title = "Todo $id",
            description = "",
            targetDate = targetDate,
            tag = null,
            isCompleted = isCompleted
        )
}