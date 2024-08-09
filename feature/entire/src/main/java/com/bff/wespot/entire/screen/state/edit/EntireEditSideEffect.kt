package com.bff.wespot.entire.screen.state.edit

import com.bff.wespot.model.ToastState

sealed class EntireEditSideEffect {
    data object NavigateToEntire : EntireEditSideEffect()
    data class ShowToast(val toastState: ToastState) : EntireEditSideEffect()
}
