package com.bff.wespot.ui.util

import android.content.Context
import com.bff.wespot.model.auth.request.KakaoAuthToken
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class KakaoLoginManager {
    suspend fun loginWithKakao(context: Context): KakaoAuthToken {
        val loginState = getKakaoLoginState(context)

        return try {
            when (loginState) {
                KaKaoLoginState.KAKAO_TALK_LOGIN -> {
                    val token = UserApiClient.loginWithKakaoTalk(context)
                    KakaoAuthToken(
                        accessToken = token.accessToken,
                    )
                }

                KaKaoLoginState.KAKAO_ACCOUNT_LOGIN -> {
                    val token = UserApiClient.loginWithKakaoAccount(context)
                    KakaoAuthToken(
                        accessToken = token.accessToken,
                    )
                }
            }
        } catch (error: Throwable) {
            if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                throw error
            }

            val token = UserApiClient.loginWithKakaoAccount(context)
            KakaoAuthToken(
                accessToken = token.accessToken,
            )
        }
    }

    private suspend fun UserApiClient.Companion.loginWithKakaoTalk(context: Context): OAuthToken {
        return suspendCancellableCoroutine { continuation ->
            instance.loginWithKakaoTalk(context) { token, error ->
                continuation.resumeTokenOrException(token, error)
            }
        }
    }

    private suspend fun UserApiClient.Companion.loginWithKakaoAccount(
        context: Context,
    ): OAuthToken {
        return suspendCancellableCoroutine { continuation ->
            instance.loginWithKakaoAccount(context) { token, error ->
                continuation.resumeTokenOrException(token, error)
            }
        }
    }

    private fun getKakaoLoginState(context: Context): KaKaoLoginState =
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

    companion object {
        fun logout() = UserApiClient.instance.logout { error ->
            if (error != null) {
                Timber.d("로그아웃 실패. SDK에서 토큰 삭제됨")
            } else {
                Timber.d("로그아웃 성공. SDK에서 토큰 삭제됨")
            }
        }
    }
}

enum class KaKaoLoginState {
    KAKAO_TALK_LOGIN,
    KAKAO_ACCOUNT_LOGIN,
}
