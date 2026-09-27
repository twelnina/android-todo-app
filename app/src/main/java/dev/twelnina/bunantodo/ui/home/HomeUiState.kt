package dev.twelnina.bunantodo.ui.home

import dev.twelnina.bunantodo.data.local.TodoEntity
import java.time.LocalDate


data class PastIncompleteItem(
    val todo: TodoEntity,
    val daysSincePlannedDate: Long
)

data class HomeUiState(
    val today: LocalDate? = null,
    val todayItems: List<TodoEntity> = emptyList(),
    val pastIncompleteItems: List<PastIncompleteItem> = emptyList()
)