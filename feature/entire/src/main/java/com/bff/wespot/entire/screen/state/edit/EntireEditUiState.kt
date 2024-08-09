package com.bff.wespot.entire.screen.state.edit

import com.bff.wespot.model.user.response.Profile

data class EntireEditUiState(
    val profile: Profile = Profile(),
    val introductionInput: String = "",
    val hasProfanity: Boolean = false,
    val isIntroductionEditing: Boolean = false,
    val isLoading: Boolean = false,
)
