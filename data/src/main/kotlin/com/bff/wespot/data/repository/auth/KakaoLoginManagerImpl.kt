package com.bff.wespot.data.repository.auth

import android.content.Context
import com.bff.wespot.domain.repository.auth.KakaoLoginManager
import com.bff.wespot.model.auth.request.KakaoAuthToken
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class KakaoLoginManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : KakaoLoginManager {
    override suspend fun loginWithKakao(): KakaoAuthToken {
        val loginState = getKakaoLoginState()

        return try {
            when (loginState) {
                KaKaoLoginState.KAKAO_TALK_LOGIN -> {

                    val token = UserApiClient.loginWithKakaoTalk()
                    KakaoAuthToken(
                        accessToken = token.accessToken,
                    )
                }

                KaKaoLoginState.KAKAO_ACCOUNT_LOGIN -> {
                    val token = UserApiClient.loginWithKakaoAccount()
                    KakaoAuthToken(
                        accessToken = token.accessToken,
                    )
                }
            }
        } catch (error: Throwable) {
            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                throw error
            }

            val token = UserApiClient.loginWithKakaoAccount()
            KakaoAuthToken(
                accessToken = token.accessToken,
            )
        }
    }

    private suspend fun UserApiClient.Companion.loginWithKakaoTalk(): OAuthToken {
        return suspendCancellableCoroutine { continuation ->
            instance.loginWithKakaoTalk(context) { token, error ->
                continuation.resumeTokenOrException(token, error)
            }
        }
    }

    private suspend fun UserApiClient.Companion.loginWithKakaoAccount(): OAuthToken {
        return suspendCancellableCoroutine { continuation ->
            instance.loginWithKakaoAccount(context) { token, error ->
                continuation.resumeTokenOrException(token, error)
            }
        }
    }

    private fun getKakaoLoginState(): KaKaoLoginState =
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            KaKaoLoginState.KAKAO_TALK_LOGIN
        } else {
            KaKaoLoginState.KAKAO_ACCOUNT_LOGIN
        }

    private fun Continuation<OAuthToken>.resumeTokenOrException(
        token: OAuthToken?,
        error: Throwable?,
    ) {
        if (error != null) {
            resumeWithException(error)
        } else if (token != null) {
            resume(token)
        } else {
            resumeWithException(RuntimeException("Failed to get kakao access token, reason is not clear."))
        }
    }
}

enum class KaKaoLoginState {
    KAKAO_TALK_LOGIN,
    KAKAO_ACCOUNT_LOGIN,
}
