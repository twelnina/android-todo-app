package com.example.todoapp.data.repository

import com.example.todoapp.data.local.TodoDao
import com.example.todoapp.data.local.TodoEntity
import kotlinx.coroutines.flow.Flow

class TodoRepository(private val todoDao: TodoDao) {
    suspend fun insert(todoEntity: TodoEntity) {
        todoDao.insert(todoEntity)
    }

    suspend fun update(todoEntity: TodoEntity) {
        todoDao.update(todoEntity)
    }

    suspend fun delete(todoEntity: TodoEntity) {
        todoDao.delete(todoEntity)
    }

    fun getAllItems(): Flow<List<TodoEntity>> {
        return todoDao.getAllItem()
    }

    suspend fun getItem(id: Int): TodoEntity? {
        return todoDao.getItem(id)
    }
}