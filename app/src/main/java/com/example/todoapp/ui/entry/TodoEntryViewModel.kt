package com.example.todoapp.ui.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todoapp.TodoApplication
import com.example.todoapp.data.repository.TodoRepository
import com.example.todoapp.model.TodoTag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TodoEntryViewModel(
    private val todoRepository: TodoRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TodoEntryUiState())
    val uiState: StateFlow<TodoEntryUiState> = _uiState.asStateFlow()

    fun onTitleChange(newTitle: String) {
        _uiState.update { currentState ->
            currentState.copy(title = newTitle)
                .let { it.copy(isEntryValid = isValid(it)) }
        }
    }

    fun onDescriptionChange(newDescription: String) {
        _uiState.update { currentState ->
            currentState.copy(description = newDescription)
                .let { it.copy(isEntryValid = isValid(it)) }
        }
    }

    fun onTargetDateChange(newTargetDate: Long?) {
        _uiState.update { currentState ->
            currentState.copy(
                targetDate = newTargetDate
            )
        }
    }

    fun onTagChange(newTag: TodoTag) {
        _uiState.update { currentState ->
            currentState.copy(
                label = if (currentState.label == newTag) null else newTag
            )
        }
    }

    private fun isValid(state: TodoEntryUiState) =
        state.title.isNotBlank() && state.description.isNotBlank()

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TodoApplication)
                val repository = application.repository
                TodoEntryViewModel(todoRepository = repository)
            }
        }
    }
}