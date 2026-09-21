package com.example.todoapp.data.time

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface CurrentDateProvider {
    fun observeDate(): Flow<LocalDate>
}