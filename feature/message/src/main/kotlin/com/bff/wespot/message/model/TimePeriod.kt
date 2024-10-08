package com.bff.wespot.message.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bff.wespot.message.R
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class TimePeriod {
    DAWN_TO_EVENING,
    EVENING_TO_NIGHT,
    NIGHT_TO_DAWN,
    ;

    val height
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            DAWN_TO_EVENING -> 352.dp
            EVENING_TO_NIGHT -> 390.dp
            NIGHT_TO_DAWN -> 352.dp
        }

    val title
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            DAWN_TO_EVENING -> stringResource(R.string.message_card_title)
            EVENING_TO_NIGHT -> stringResource(R.string.message_card_title)
            NIGHT_TO_DAWN -> stringResource(R.string.message_title_night)
        }

    val imageRes
        get() = when (this) {
            DAWN_TO_EVENING -> R.raw.message_evening
            EVENING_TO_NIGHT -> R.raw.message_evening
            NIGHT_TO_DAWN -> R.raw.message_dawn
        }
}

internal fun getCurrentTimePeriod(
    messageStartTime: String,
    messageReceiveTime: String,
): TimePeriod {
    val currentTime = LocalTime.now(ZoneId.of("Asia/Seoul"))
    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    val dawnStartTime = LocalTime.of(0, 0)
    val eveningStartTime = LocalTime.parse(messageStartTime, formatter)
    val nightStartTime = LocalTime.parse(messageReceiveTime, formatter)

    return when {
        currentTime.isBetween(dawnStartTime, eveningStartTime) -> TimePeriod.DAWN_TO_EVENING
        currentTime.isBetween(eveningStartTime, nightStartTime) -> TimePeriod.EVENING_TO_NIGHT
        else -> TimePeriod.NIGHT_TO_DAWN
    }
}

private fun LocalTime.isBetween(startTime: LocalTime, endTime: LocalTime): Boolean {
    return this >= startTime && this < endTime
}
