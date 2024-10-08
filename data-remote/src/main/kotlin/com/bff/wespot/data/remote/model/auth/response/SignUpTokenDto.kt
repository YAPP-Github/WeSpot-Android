package com.bff.wespot.data.remote.model.auth.response

import com.bff.wespot.model.auth.response.SignUpToken
import kotlinx.serialization.Serializable

@Serializable
data class SignUpTokenDto(
    val signUpToken: String,
) {
    fun toSignUpToken() = SignUpToken(
        signUpToken = signUpToken,
    )
}