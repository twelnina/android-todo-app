package com.example.todoapp.ui.component.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.todoapp.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAlertDialog(
    showDeleteDialog: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = stringResource(R.string.delete_confirmation_title)) },
            text = { Text(text = stringResource(R.string.delete_confirmation_message)) },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(
                        text = stringResource(R.string.cancel)
                    )
                }
            }
        )
    }
}