package com.example.todoapp.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todoapp.TodoApplication
import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.data.repository.TodoRepository
import com.example.todoapp.data.time.CurrentDateProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class CalendarViewModel(
    private val repository: TodoRepository, private val currentDateProvider: CurrentDateProvider
) : ViewModel() {
    private val _selectedDate = MutableStateFlow<LocalDate?>(null)

    val uiState: StateFlow<CalendarUiState> = combine(
        currentDateProvider.observeDate(),
        repository.getAllItems(),
        _selectedDate
    ) { today, todos, selectedDate ->
        val date = selectedDate ?: today

        val todoCountsByDate = todos
            .mapNotNull { it.targetDate }
            .groupingBy { it }
            .eachCount()

        CalendarUiState(
            today = today,
            selectedDate = date,
            todos = todos,
            selectedDateTodos = todos.filter { it.targetDate == date },
            todoCountsByDate = todoCountsByDate
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CalendarUiState()
    )

    fun onDateSelected(newDate: LocalDate) {
        _selectedDate.value = newDate
    }

    fun onPreviousDate(minDate: LocalDate) {
        _selectedDate.update { selectedDate ->
            val date = selectedDate ?: uiState.value.selectedDate
            if (date != null && date.isAfter(minDate)) date.minusDays(1) else selectedDate
        }
    }

    fun onNextDate(maxDate: LocalDate) {
        _selectedDate.update { selectedDate ->
            val date = selectedDate ?: uiState.value.selectedDate
            if (date != null && date.isBefore(maxDate)) date.plusDays(1) else selectedDate
        }
    }

    fun updateCompleted(todo: TodoEntity, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.update(
                todo.copy(isCompleted = isCompleted)
            )
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TodoApplication)
                val repository = application.repository
                CalendarViewModel(
                    repository = repository,
                    currentDateProvider = application.currentDateProvider
                )
            }
        }
    }
}
