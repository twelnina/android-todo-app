package dev.twelnina.bunantodo

import android.app.Application
import androidx.room.Room
import dev.twelnina.bunantodo.data.local.AppDatabase
import dev.twelnina.bunantodo.data.repository.TodoRepository
import dev.twelnina.bunantodo.data.time.CurrentDateProvider
import dev.twelnina.bunantodo.data.time.SystemCurrentDateProvider

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

    val currentDateProvider: CurrentDateProvider by lazy {
        SystemCurrentDateProvider(applicationContext)
    }
}