package com.example.todoapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todoapp.TodoApplication
import com.example.todoapp.data.repository.TodoRepository
import com.example.todoapp.model.TodoTag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class TodoHomeViewModel(
    private val todoRepository: TodoRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private val _selectedTags = MutableStateFlow<Set<TodoTag>>(emptySet())

    val uiState: StateFlow<TodoHomeUiState> = combine(
        todoRepository.getAllItems(),
        _searchQuery,
        _selectedTags
    ) { items, query, selectedTags ->
        TodoHomeUiState(
            searchQuery = query,
            selectedTags = selectedTags,
            todoEntities = items.filter { item ->
                val matchesQuery =
                    item.title.contains(query, ignoreCase = true) ||
                            item.description.contains(query, ignoreCase = true)
                val matchesTag = if (selectedTags.isEmpty()) {
                    true
                } else {
                    item.tag in selectedTags
                }
                matchesQuery && matchesTag
            }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TodoHomeUiState()
    )

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onTagSelected(tag: TodoTag) {
        _selectedTags.update { currentState ->
            if (tag in currentState) currentState - tag else currentState + tag
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TodoApplication)
                val repository = application.repository
                TodoHomeViewModel(todoRepository = repository)
            }
        }
    }
}