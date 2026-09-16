package com.example.todoapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todoapp.TodoApplication
import com.example.todoapp.data.repository.TodoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class HomeViewModel(private val repository: TodoRepository) : ViewModel() {
    private val today = LocalDate.now()

    val uiState: StateFlow<HomeUiState> = combine(
        repository.observeTodayItems(today),
        repository.observeOverdueItems(today)
    ) { todayItems, overdueItems ->
        HomeUiState(
            todayItems = todayItems,
            overdueItems = overdueItems.map { entity ->
                val targetDate = entity.targetDate
                OverdueItem(
                    todo = entity,
                    daysOverdue = ChronoUnit.DAYS.between(targetDate, today)
                )
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState()
    )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TodoApplication)
                val repository = application.repository
                HomeViewModel(repository = repository)
            }
        }
    }
}