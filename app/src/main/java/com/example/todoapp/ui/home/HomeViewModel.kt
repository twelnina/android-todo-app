package com.example.todoapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todoapp.TodoApplication
import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.data.repository.TodoRepository
import com.example.todoapp.data.time.CurrentDateProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class HomeViewModel(
    private val repository: TodoRepository,
    private val currentDateProvider: CurrentDateProvider
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> =
        currentDateProvider.observeDate().flatMapLatest { today ->
            combine(
                repository.observeTodayItems(today),
                repository.observeOverdueItems(today)
            ) { todayItems, overdueItems ->
                HomeUiState(
                    today = today,
                    todayItems = todayItems,
                    overdueItems = overdueItems.map { entity ->
                        val targetDate = entity.targetDate
                        OverdueItem(
                            todo = entity,
                            daysOverdue = ChronoUnit.DAYS.between(targetDate, today)
                        )
                    }
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState()
        )

    fun updateCompleted(todo: TodoEntity, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.update(
                todo.copy(isCompleted = isCompleted)
            )
        }
    }

    fun updateTargetDate(todo: TodoEntity, newTargetDate: LocalDate) {
        viewModelScope.launch {
            repository.update(
                todo.copy(targetDate = newTargetDate)
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TodoApplication)
                val repository = application.repository
                HomeViewModel(
                    repository = repository,
                    currentDateProvider = application.currentDateProvider
                )
            }
        }
    }
}
