package com.example.todoapp.data.local

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
    suspend fun insert(todoEntity: TodoEntity)

    @Update
    suspend fun update(todoEntity: TodoEntity)

    @Delete
    suspend fun delete(todoEntity: TodoEntity)

    @Query("SELECT * FROM todo_items ORDER BY targetDate")
    fun getAllItem(): Flow<List<TodoEntity>>

    @Query("SELECT * FROM todo_items WHERE label = :tag")
    fun getItemByTag(tag: TodoTag): Flow<List<TodoEntity>>
}