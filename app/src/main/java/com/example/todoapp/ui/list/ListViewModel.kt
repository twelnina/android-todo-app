package com.example.todoapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todoapp.TodoApplication
import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.data.repository.TodoRepository
import com.example.todoapp.model.PlannedDateFilter
import com.example.todoapp.model.TodoTag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class ListViewModel(
    private val todoRepository: TodoRepository,
    initialPlannedDateFilter: PlannedDateFilter = PlannedDateFilter.ALL
) : ViewModel() {
    private val _searchQuery = MutableStateFlow("")
    private val _selectedTags = MutableStateFlow<Set<TodoTag>>(emptySet())
    private val _selectedPlannedDateFilter = MutableStateFlow(initialPlannedDateFilter)
    private val _showBottomSheet = MutableStateFlow(false)

    val uiState: StateFlow<ListUiState> = combine(
        todoRepository.getAllItems(),
        _searchQuery,
        _selectedTags,
        _selectedPlannedDateFilter,
        _showBottomSheet
    ) { items, query, selectedTags, plannedDateFilter, showSheet ->
        val filteredTodos = filterTodos(
            items = items,
            query = query,
            tags = selectedTags,
            plannedDateFilter = plannedDateFilter
        )

        ListUiState(
            searchQuery = query,
            selectedTags = selectedTags,
            selectedPlannedDateFilter = plannedDateFilter,
            showBottomSheet = showSheet,
            todoGroups = groupTodosByDate(filteredTodos)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ListUiState(
            selectedPlannedDateFilter = initialPlannedDateFilter
        )
    )

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onTagSelected(tag: TodoTag) {
        _selectedTags.update { currentState ->
            if (tag in currentState) currentState - tag else currentState + tag
        }
    }

    fun onPlannedDateFilterSelected(filter: PlannedDateFilter) {
        _selectedPlannedDateFilter.value = filter
        _showBottomSheet.value = false
    }

    fun showBottomSheet() {
        _showBottomSheet.value = true
    }

    fun dismissBottomSheet() {
        _showBottomSheet.value = false
    }

    fun updateCompleted(todo: TodoEntity, isCompleted: Boolean) {
        viewModelScope.launch {
            todoRepository.update(
                todo.copy(isCompleted = isCompleted)
            )
        }
    }

    companion object {
        fun createFactory(
            initialPlannedDateFilter: PlannedDateFilter = PlannedDateFilter.ALL
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TodoApplication)
                val repository = application.repository

                ListViewModel(
                    todoRepository = repository,
                    initialPlannedDateFilter = initialPlannedDateFilter
                )
            }
        }
    }
}


internal fun filterTodos(
    items: List<TodoEntity>,
    query: String,
    tags: Set<TodoTag>,
    plannedDateFilter: PlannedDateFilter,
    today: LocalDate = LocalDate.now()
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
        val matchesDate = when (plannedDateFilter) {
            PlannedDateFilter.ALL -> true
            PlannedDateFilter.TODAY -> item.plannedDate == today
            PlannedDateFilter.TOMORROW -> item.plannedDate == today.plusDays(1)
            PlannedDateFilter.THIS_WEEK -> {
                val endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                item.plannedDate?.let { date ->
                    (date.isEqual(today) || date.isAfter(today)) &&
                            (date.isEqual(endOfWeek) || date.isBefore(endOfWeek))
                } ?: false
            }

            PlannedDateFilter.PAST_INCOMPLETE -> !item.isCompleted &&
                    (item.plannedDate?.isBefore(today) ?: false)

            PlannedDateFilter.UNSCHEDULED -> item.plannedDate == null
        }

        matchesQuery && matchesTag && matchesDate
    }
}

internal fun groupTodosByDate(todos: List<TodoEntity>): List<TodoDateGroup> {
    return todos
        .groupBy { todo -> todo.plannedDate }
        .map { (date, todoForDate) ->
            TodoDateGroup(date, todoForDate)
        }
        .sortedWith { firstGroup, secondGroup ->
            when {
                firstGroup.date == null && secondGroup.date == null -> 0
                firstGroup.date == null -> 1
                secondGroup.date == null -> -1
                else -> firstGroup.date.compareTo(secondGroup.date)
            }
        }
}