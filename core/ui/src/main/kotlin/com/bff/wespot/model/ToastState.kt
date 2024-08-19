package com.bff.wespot.model

import androidx.annotation.StringRes
import com.bff.wespot.designsystem.component.indicator.WSToastType

data class ToastState(
    val show: Boolean,
    @StringRes val message: Int,
    val type: WSToastType,
) {
    constructor() :
            this(false, com.bff.wespot.designsystem.R.string.cancel, WSToastType.Success)
}
