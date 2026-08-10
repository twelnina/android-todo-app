package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.todoapp.ui.TodoEntryScreenContent
import com.example.todoapp.ui.TodoScreen
import com.example.todoapp.ui.theme.ToDoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val backStack = rememberNavBackStack(
                AppNavKey.TodoList
            )
            ToDoAppTheme {
                NavDisplay(
                    backStack = backStack,
                    onBack = { backStack.removeLastOrNull() },
                    entryProvider = entryProvider {
                        entry<AppNavKey.TodoList> {
                            TodoScreen(onAddTodo = { backStack.add(AppNavKey.AddTodo) })
                        }
                        entry<AppNavKey.AddTodo> {
                            TodoEntryScreenContent(onBack = { backStack.removeLastOrNull() })
                        }
                    }
                )
            }
        }
    }
}