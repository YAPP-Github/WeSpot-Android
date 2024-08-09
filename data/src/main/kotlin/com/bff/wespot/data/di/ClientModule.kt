package com.bff.wespot.data.di

import android.content.Context
import com.bff.wespot.data.BuildConfig
import com.bff.wespot.data.remote.model.auth.response.AuthTokenDto
import com.bff.wespot.data.remote.model.auth.response.SignUpTokenDto
import com.bff.wespot.domain.repository.DataStoreRepository
import com.bff.wespot.domain.util.DataStoreKey
import com.bff.wespot.navigation.Navigator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.AttributeKey
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ClientModule {
    private const val REQUEST_TIMEOUT_MILLIS = 50_000L

    @Provides
    @Singleton
    fun provideHttpClient(
        @ApplicationContext context: Context,
        navigator: Navigator,
        repository: DataStoreRepository,
    ): HttpClient = HttpClient(CIO) {
        defaultRequest {
            header(HttpHeaders.ContentType, ContentType.Application.Json)
            url(BuildConfig.BASE_URL)
        }

        install(HttpTimeout) {
            requestTimeoutMillis = REQUEST_TIMEOUT_MILLIS
        }

        install(ContentNegotiation) {
            json(
                json = Json {
                    encodeDefaults = true // Default 값 포함한 직렬화 수행
                    isLenient = true // 비표준 Json 형식 허용
                    prettyPrint = true
                    ignoreUnknownKeys = true
                }
            )
        }

        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    if (BuildConfig.DEBUG) {
                        Timber.i(message)
                    }
                }
            }
        }

        HttpResponseValidator {
            this.validateResponse {
                if (it.request.url.toString().contains("api/v1/auth/login")) {
                    when (it.status.value) {
                        200 -> {
                            val parsedBody = it.body<AuthTokenDto>()
                            it.call.attributes.put(AttributeKey("parsedBody"), parsedBody)
                        }

                        202 -> {
                            val parsedBody = it.body<SignUpTokenDto>()
                            it.call.attributes.put(AttributeKey("parsedBody"), parsedBody)
                        }
                    }
                }
            }
        }

        install(Auth) {
            bearer {
                refreshTokens {
                    val expire =
                        repository.getString(DataStoreKey.REFRESH_TOKEN_EXPIRED_AT)
                            .first()

                    if (checkExpire(expire)) {
                        repository.clear()
                        navigator.navigateToAuth(context)
                    }

                    val refreshToken = repository.getString(DataStoreKey.REFRESH_TOKEN)
                        .first()
                    val token = client.post {
                        markAsRefreshTokenRequest()
                        url("auth/reissue")
                        setBody {
                            "refreshToken" to refreshToken
                        }
                    }.body<AuthTokenDto>()

                    repository.saveString(
                        DataStoreKey.REFRESH_TOKEN,
                        token.refreshToken
                    )

                    repository.saveString(
                        DataStoreKey.REFRESH_TOKEN_EXPIRED_AT,
                        token.refreshTokenExpiredAt
                    )

                    repository.saveString(
                        DataStoreKey.ACCESS_TOKEN,
                        token.accessToken
                    )

                    BearerTokens(
                        accessToken = token.accessToken,
                        refreshToken = token.refreshToken
                    )
                }

                loadTokens {
                    val accessToken = repository.getString(DataStoreKey.ACCESS_TOKEN)
                        .first()

                    val refreshToken = repository.getString(DataStoreKey.REFRESH_TOKEN)
                        .first()
                    BearerTokens(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                    )
                }
            }
        }
    }
}

private fun checkExpire(expire: String): Boolean {
    if (expire.isEmpty()) {
        return true
    }

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS")
    return try {
        val givenTime = LocalDateTime.parse(expire, formatter)

        val currentTime = LocalDateTime.now()

        givenTime.isBefore(currentTime)
    } catch (e: Exception) {
        true
    }
}