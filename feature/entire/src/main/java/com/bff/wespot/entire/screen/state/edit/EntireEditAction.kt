package com.bff.wespot.entire.screen.state.edit

import com.bff.wespot.model.user.response.ProfileCharacter

sealed class EntireEditAction {
    data object OnCharacterEditScreenEntered : EntireEditAction()
    data object OnIntroductionEditDoneButtonClicked : EntireEditAction()
    data class OnProfileEditScreenEntered(val isCompleteEdit: Boolean) : EntireEditAction()
    data class OnProfileEditTextFieldFocused(val focused: Boolean) : EntireEditAction()
    data class OnIntroductionChanged(val introduction: String) : EntireEditAction()
    data class OnCharacterEditDoneButtonClicked(
        val character: ProfileCharacter,
    ) : EntireEditAction()
}
