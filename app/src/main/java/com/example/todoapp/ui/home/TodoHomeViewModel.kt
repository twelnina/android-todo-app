package com.example.todoapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todoapp.TodoApplication
import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.data.repository.TodoRepository
import com.example.todoapp.model.DueDateFilter
import com.example.todoapp.model.TodoTag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class TodoHomeViewModel(
    private val todoRepository: TodoRepository
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private val _selectedTags = MutableStateFlow<Set<TodoTag>>(emptySet())
    private val _selectedDueDateFilter = MutableStateFlow(DueDateFilter.ALL)
    private val _showBottomSheet = MutableStateFlow(false)

    val uiState: StateFlow<TodoHomeUiState> = combine(
        todoRepository.getAllItems(),
        _searchQuery,
        _selectedTags,
        _selectedDueDateFilter,
        _showBottomSheet
    ) { items, query, selectedTags, dueDateFilter, showSheet ->
        TodoHomeUiState(
            searchQuery = query,
            selectedTags = selectedTags,
            selectedDueDateFilter = dueDateFilter,
            showBottomSheet = showSheet,
            todoEntities = filterTodos(
                items = items,
                query = query,
                tags = selectedTags,
                dueDate = dueDateFilter
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TodoHomeUiState()
    )

    private fun filterTodos(
        items: List<TodoEntity>,
        query: String,
        tags: Set<TodoTag>,
        dueDate: DueDateFilter
    ): List<TodoEntity> {
        return items.filter { item ->
            val matchesQuery =
                item.title.contains(query, ignoreCase = true) ||
                        item.description.contains(query, ignoreCase = true)
            val matchesTag = if (tags.isEmpty()) {
                true
            } else {
                item.tag in tags
            }
            val matchesDate = when (dueDate) {
                DueDateFilter.ALL -> true
                DueDateFilter.TODAY -> item.targetDate == LocalDate.now()
                DueDateFilter.TOMORROW -> item.targetDate == LocalDate.now().plusDays(1)
                DueDateFilter.THIS_WEEK -> {
                    val today = LocalDate.now()
                    val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                    item.targetDate?.let { date ->
                        (date.isEqual(today) || date.isAfter(today)) &&
                                (date.isEqual(endOfWeek) || date.isBefore(endOfWeek))
                    } ?: false
                }

                DueDateFilter.OVERDUE -> item.targetDate?.isBefore(LocalDate.now()) ?: false
                DueDateFilter.NO_DATE -> item.targetDate == null
            }

            matchesQuery && matchesTag && matchesDate
        }
    }

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
                TodoHomeViewModel(todoRepository = repository)
            }
        }
    }
}