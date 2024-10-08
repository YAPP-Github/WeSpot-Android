package com.bff.wespot.state

import com.bff.wespot.model.common.Restriction

data class MainUiState (
    val isPushNotificationNavigation: Boolean = false,
    val userId: String = "",
    val restriction: Restriction = Restriction.Empty,
    val kakaoChannel: String,
)
