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
                repository.observePastIncompleteItems(today)
            ) { todayItems, pastIncompleteItems ->
                HomeUiState(
                    today = today,
                    todayItems = todayItems,
                    pastIncompleteItems = pastIncompleteItems.map { entity ->
                        val plannedDate = entity.plannedDate
                        PastIncompleteItem(
                            todo = entity,
                            daysSincePlannedDate = ChronoUnit.DAYS.between(plannedDate, today)
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

    fun updatePlannedDate(todo: TodoEntity, newPlannedDate: LocalDate) {
        viewModelScope.launch {
            repository.update(
                todo.copy(plannedDate = newPlannedDate)
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
