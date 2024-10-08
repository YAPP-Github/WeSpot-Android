package com.bff.wespot.domain.usecase

import com.bff.wespot.domain.repository.DataStoreRepository
import com.bff.wespot.domain.repository.auth.AuthRepository
import com.bff.wespot.domain.repository.firebase.messaging.MessagingRepository
import com.bff.wespot.domain.util.DataStoreKey
import com.bff.wespot.model.auth.request.KakaoAuthToken
import com.bff.wespot.model.auth.response.AuthToken
import com.bff.wespot.model.auth.response.SignUpToken
import com.bff.wespot.model.constants.LoginState
import javax.inject.Inject

class KakaoLoginUseCase @Inject constructor(
    private val messagingRepository: MessagingRepository,
    private val authRepository: AuthRepository,
    private val dataStoreRepository: DataStoreRepository,
) {
    suspend operator fun invoke(result: KakaoAuthToken): Result<LoginState> {
        val token = messagingRepository.getFcmToken()
        dataStoreRepository.saveString(DataStoreKey.PUSH_TOKEN, token)

        return authRepository.sendKakaoToken(result)
            .map {
                when (it) {
                    is AuthToken -> {
                        dataStoreRepository.saveString(DataStoreKey.ACCESS_TOKEN, it.accessToken)
                        dataStoreRepository.saveString(DataStoreKey.REFRESH_TOKEN, it.refreshToken)
                        dataStoreRepository.saveString(
                            DataStoreKey.REFRESH_TOKEN_EXPIRED_AT,
                            it.refreshTokenExpiredAt,
                        )
                        LoginState.LOGIN_SUCCESS
                    }

                    is SignUpToken -> {
                        dataStoreRepository.saveString(DataStoreKey.SIGN_UP_TOKEN, it.signUpToken)
                        LoginState.LOGIN_FAILURE
                    }

                    else -> {
                        LoginState.LOGIN_FAILURE
                    }
                }
            }
    }
}
