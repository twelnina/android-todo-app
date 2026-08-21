package com.example.todoapp.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todoapp.TodoApplication
import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.data.repository.TodoRepository
import com.example.todoapp.model.TodoTag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class EditViewModel(private val todoRepository: TodoRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(EditUiState())
    val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()
    fun loadItem(id: Int) {
        viewModelScope.launch {
            todoRepository.getItem(id)?.run {
                _uiState.update { currentState ->
                    currentState.copy(
                        id = id,
                        title = title,
                        description = description,
                        targetDate = targetDate.toEpochMillis(),
                        selectedTag = tag,
                    )
                }
            }
        }
    }

    fun updateTodo() {
        uiState.value.run {
            viewModelScope.launch {
                todoRepository.update(
                    TodoEntity(
                        id = id,
                        title = title,
                        description = description,
                        targetDate = targetDate.toLocalDate(),
                        tag = selectedTag
                    )
                )
            }
        }
    }

    fun deleteTodo() {
        uiState.value.run {
            viewModelScope.launch {
                todoRepository.delete(
                    TodoEntity(
                        id = id,
                        title = title,
                        description = description,
                        targetDate = targetDate.toLocalDate(),
                        tag = selectedTag
                    )
                )
            }
        }
    }

    fun updateTitle(newTitle: String) {
        _uiState.update { currentState ->
            currentState.copy(title = newTitle)
        }
    }

    fun updateDescription(newDescription: String) {
        _uiState.update { currentState ->
            currentState.copy(description = newDescription)
        }
    }

    fun updateTargetDate(newTargetDate: Long?) {
        _uiState.update { currentState ->
            currentState.copy(targetDate = newTargetDate)
        }
    }

    fun updateSelectedTag(newSelectedTag: TodoTag?) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedTag = if (currentState.selectedTag == newSelectedTag) null else newSelectedTag
            )
        }
    }

    private fun LocalDate?.toEpochMillis(): Long? =
        this?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()

    private fun Long?.toLocalDate(): LocalDate? =
        this?.let { millis ->
            Instant.ofEpochMilli(millis)
                .atZone(ZoneOffset.UTC)
                .toLocalDate()
        }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TodoApplication)
                val repository = application.repository
                EditViewModel(repository)
            }
        }
    }
}