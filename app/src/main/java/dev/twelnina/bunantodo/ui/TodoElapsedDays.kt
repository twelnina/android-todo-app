package dev.twelnina.bunantodo.ui

import dev.twelnina.bunantodo.data.local.TodoEntity
import java.time.LocalDate
import java.time.temporal.ChronoUnit

internal fun TodoEntity.elapsedDaysSincePlannedDate(today: LocalDate): Long? {
    val plannedDate = plannedDate ?: return null

    if (isCompleted || !plannedDate.isBefore(today)) {
        return null
    }

    return ChronoUnit.DAYS.between(plannedDate, today)
}
