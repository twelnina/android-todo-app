package com.example.todoapp.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todoapp.TodoApplication
import com.example.todoapp.data.repository.TodoRepository
import com.example.todoapp.model.TodoTag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset

class EditViewModel(private val todoRepository: TodoRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(EditUiState())
    val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()
    fun loadItem(id: Int) {
        viewModelScope.launch {
            val entity = todoRepository.getItem(id)
            if (entity != null) {
                _uiState.update { currentState ->
                    currentState.copy(
                        title = entity.title,
                        description = entity.description,
                        targetDate = entity.targetDate.toEpochMillis(),
                        selectedTag = entity.tag,
                    )
                }
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
            currentState.copy(selectedTag = newSelectedTag)
        }
    }

    private fun LocalDate?.toEpochMillis(): Long? =
        this?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()

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