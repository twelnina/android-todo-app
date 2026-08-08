package com.example.todoapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.todoapp.model.TodoTag
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Insert
    suspend fun insert(todoItem: TodoItem)

    @Update
    suspend fun update(todoItem: TodoItem)

    @Delete
    suspend fun delete(todoItem: TodoItem)

    @Query("SELECT * FROM todo_items ORDER BY date")
    fun getAllItem(): Flow<List<TodoItem>>

    @Query("SELECT * FROM todo_items WHERE label = :tag")
    fun getItemByTag(tag: TodoTag): Flow<List<TodoItem>>
}