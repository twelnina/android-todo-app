package com.example.todoapp.ui.entry

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

class AddViewModel(private val todoRepository: TodoRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AddUiState())
    val uiState: StateFlow<AddUiState> = _uiState.asStateFlow()

    fun updateTitle(newTitle: String) {
        _uiState.update { currentState ->
            currentState.copy(title = newTitle)
                .let { it.copy(isEntryValid = isValid(it)) }
        }
    }

    fun updateDescription(newDescription: String) {
        _uiState.update { currentState ->
            currentState.copy(description = newDescription)
                .let { it.copy(isEntryValid = isValid(it)) }
        }
    }

    fun updateTargetDate(newTargetDate: Long?) {
        _uiState.update { currentState ->
            currentState.copy(
                targetDate = newTargetDate
            )
        }
    }

    fun updateSelectedTag(newTag: TodoTag) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedTag = if (currentState.selectedTag == newTag) null else newTag
            )
        }
    }

    fun saveTodo(onSaved: () -> Unit) {
        uiState.value.run {
            viewModelScope.launch {
                todoRepository.insert(
                    TodoEntity(
                        title = title,
                        description = description,
                        targetDate = targetDate?.toLocalDate(),
                        tag = selectedTag
                    )
                )
                onSaved()
            }
        }
    }

    private fun Long?.toLocalDate(): LocalDate? =
        this?.let { millis ->
            Instant.ofEpochMilli(millis)
                .atZone(ZoneOffset.UTC)
                .toLocalDate()
        }

    fun isValid(state: AddUiState) =
        state.title.isNotBlank() && state.description.isNotBlank()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TodoApplication)
                val repository = application.repository
                AddViewModel(todoRepository = repository)
            }
        }
    }
}