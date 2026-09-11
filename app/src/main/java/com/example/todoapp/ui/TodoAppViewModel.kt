package com.example.todoapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.todoapp.TodoApplication
import com.example.todoapp.data.local.TodoEntity
import com.example.todoapp.data.repository.TodoRepository
import kotlinx.coroutines.launch

class TodoAppViewModel(
    private val todoRepository: TodoRepository
) : ViewModel() {

    fun restoreDeletedTodo(todo: TodoEntity) {
        viewModelScope.launch {
            todoRepository.insert(todo)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                as TodoApplication

                TodoAppViewModel(application.repository)
            }
        }
    }
}