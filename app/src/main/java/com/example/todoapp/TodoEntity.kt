package com.example.todoapp

import android.icu.text.CaseMap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoapp.model.TodoTag
import java.time.LocalDate

@Entity(tableName = "todo_items")
data class TodoItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val detail: String,
    val date: LocalDate,
    val label: TodoTag
)