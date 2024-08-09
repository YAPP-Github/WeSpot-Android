package com.bff.wespot.model.auth.request

data class KakaoAuthToken(
    val accessToken: String,
    val socialType: String = "KAKAO",
)
