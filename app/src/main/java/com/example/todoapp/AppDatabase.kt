package com.example.todoapp

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [TodoItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TodoTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}