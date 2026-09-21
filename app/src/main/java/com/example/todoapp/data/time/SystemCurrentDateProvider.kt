package com.example.todoapp.data.time

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDate

class SystemCurrentDateProvider(
    context: Context,
    private val currentDate: () -> LocalDate = { LocalDate.now() }
) : CurrentDateProvider {
    private val applicationContext = context.applicationContext

    override fun observeDate(): Flow<LocalDate> =
        callbackFlow {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    trySend(currentDate())
                }
            }

            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_DATE_CHANGED)
                addAction(Intent.ACTION_TIME_CHANGED)
                addAction(Intent.ACTION_TIMEZONE_CHANGED)
            }

            ContextCompat.registerReceiver(
                applicationContext,
                receiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )

            trySend(currentDate())

            awaitClose {
                applicationContext.unregisterReceiver(receiver)
            }
        }
            .conflate()
            .distinctUntilChanged()
            .flowOn(Dispatchers.Main.immediate)
}