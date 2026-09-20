package com.example.todoapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todoapp.TodoApplication
import com.example.todoapp.data.repository.TodoRepository
import com.example.todoapp.model.DueDateFilter
import com.example.todoapp.model.TodoTag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ListViewModel(
    private val todoRepository: TodoRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private val _selectedTags = MutableStateFlow<Set<TodoTag>>(emptySet())
    private val _selectedDueDateFilter = MutableStateFlow(DueDateFilter.ALL)
    private val _showBottomSheet = MutableStateFlow(false)

    val uiState: StateFlow<ListUiState> = combine(
        todoRepository.getAllItems(),
        _searchQuery,
        _selectedTags,
        _selectedDueDateFilter,
        _showBottomSheet
    ) { items, query, selectedTags, dueDateFilter, showSheet ->
        val filteredTodos = filterTodos(
            items = items,
            query = query,
            tags = selectedTags,
            dueDate = dueDateFilter
        )

        ListUiState(
            searchQuery = query,
            selectedTags = selectedTags,
            selectedDueDateFilter = dueDateFilter,
            showBottomSheet = showSheet,
            todoGroups = groupTodosByDate(filteredTodos)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ListUiState()
    )

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onTagSelected(tag: TodoTag) {
        _selectedTags.update { currentState ->
            if (tag in currentState) currentState - tag else currentState + tag
        }
    }

    fun onDueDateFilterSelected(filter: DueDateFilter) {
        _selectedDueDateFilter.value = filter
        _showBottomSheet.value = false
    }

    fun showBottomSheet() {
        _showBottomSheet.value = true
    }

    fun dismissBottomSheet() {
        _showBottomSheet.value = false
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TodoApplication)
                val repository = application.repository
                ListViewModel(todoRepository = repository)
            }
        }
    }
}