package com.example.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.ui.TodoScreen
import com.example.todoapp.ui.TodoSearchBar
import com.example.todoapp.ui.TodoViewModel
import com.example.todoapp.ui.theme.ToDoAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ToDoAppTheme {
                val viewModel: TodoViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                Scaffold(
                    topBar = {
                        TodoSearchBar(
                            query = uiState.searchQuery,
                            onQueryChange = { viewModel.onQueryChange(it) }
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    TodoScreen(
                        todoList = uiState.todoItems,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    )
                }
            }
        }
    }
}