package com.example.todoapp.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.todoapp.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun DateChangeSummary(
    previousDate: LocalDate?,
    newDate: LocalDate?,
    modifier: Modifier = Modifier
) {
    val locale = LocalConfiguration.current.locales[0]

    val formatter = remember(locale) {
        DateTimeFormatter.ofPattern("MMM d, yyyy", locale)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DateSummaryItem(
            label = stringResource(R.string.previous),
            date = previousDate?.format(formatter)
                ?: stringResource(R.string.no_date),
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(R.drawable.arrow_right_alt_24px),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        DateSummaryItem(
            label = stringResource(R.string.new_date),
            date = newDate?.format(formatter)
                ?: stringResource(R.string.no_date),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DateSummaryItem(
    label: String,
    date: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = date,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}