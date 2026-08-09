package com.example.todoapp

import com.example.todoapp.model.TodoTag
import kotlinx.coroutines.flow.Flow

class TodoRepository(
    private val todoDao: TodoDao
) {
    suspend fun insert(todoItem: TodoItem) {
        todoDao.insert(todoItem)
    }
    suspend fun update(todoItem: TodoItem) {
        todoDao.update(todoItem)
    }
    suspend fun delete(todoItem: TodoItem) {
        todoDao.delete(todoItem)
    }
    fun getAllItems() : Flow<List<TodoItem>> {
        return todoDao.getAllItem()
    }
    fun getItemsByTag(tag: TodoTag) : Flow<List<TodoItem>> {
        return todoDao.getItemByTag(tag)
    }
}