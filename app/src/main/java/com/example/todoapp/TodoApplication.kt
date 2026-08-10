package com.example.todoapp

import android.app.Application
import androidx.room.Room
import com.example.todoapp.data.local.AppDatabase
import com.example.todoapp.data.repository.TodoRepository

class TodoApplication : Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "todo_database"
        ).build()
    }

    val repository: TodoRepository by lazy {
        TodoRepository(database.todoDao())
    }
}