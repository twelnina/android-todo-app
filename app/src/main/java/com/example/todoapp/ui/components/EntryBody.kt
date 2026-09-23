package com.example.todoapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.todoapp.R
import com.example.todoapp.model.TodoTag

@Composable
fun TodoEntryBody(
    title: String,
    description: String,
    selectedTag: TodoTag?,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onTagChange: (TodoTag) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextField(
            value = title,
            onValueChange = onTitleChange,
            label = { Text(text = stringResource(R.string.title)) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            modifier = Modifier.fillMaxWidth()
        )
        TextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text(text = stringResource(R.string.description)) },
            minLines = 5,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ),
            modifier = Modifier.fillMaxWidth()
        )
        TagSelector(
            onTagChange = onTagChange,
            selectedTag = selectedTag,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
