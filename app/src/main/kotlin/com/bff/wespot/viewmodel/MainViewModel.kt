package com.bff.wespot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bff.wespot.MainScreenNavArgs
import com.bff.wespot.analytic.AnalyticsEvent
import com.bff.wespot.analytic.AnalyticsHelper
import com.bff.wespot.domain.repository.CommonRepository
import com.bff.wespot.domain.repository.DataStoreRepository
import com.bff.wespot.domain.repository.firebase.config.RemoteConfigRepository
import com.bff.wespot.domain.repository.user.UserRepository
import com.bff.wespot.domain.usecase.CacheProfileUseCase
import com.bff.wespot.domain.util.DataStoreKey
import com.bff.wespot.domain.util.RemoteConfigKey
import com.bff.wespot.state.MainAction
import com.bff.wespot.state.MainSideEffect
import com.bff.wespot.state.MainUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val cacheProfileUseCase: CacheProfileUseCase,
    private val dataStoreRepository: DataStoreRepository,
    private val userRepository: UserRepository,
    private val coroutineDispatcher: CoroutineDispatcher,
    private val analyticsHelper: AnalyticsHelper,
    private val commonRepository: CommonRepository,
    private val remoteConfigRepository: RemoteConfigRepository,
) : ViewModel(), ContainerHost<MainUiState, MainSideEffect> {
    override val container = container<MainUiState, MainSideEffect>(MainUiState(
        kakaoChannel = remoteConfigRepository.fetchFromRemoteConfig(RemoteConfigKey.WESPOT_KAKAO_CHANNEL_URL)
    ))

    init {
        viewModelScope.launch {
            dataStoreRepository.getString(DataStoreKey.ID).collect {
                intent {
                    if (it.isNotEmpty()) {
                        reduce {
                            state.copy(userId = it)
                        }
                    }
                }
            }
        }
    }

    fun onAction(action: MainAction) {
        when (action) {
            MainAction.OnMainScreenEntered -> handleMainScreenEntered()
            MainAction.OnNavigateByPushNotification -> handleNavigateByPushNotification()
            is MainAction.OnEnteredByPushNotification -> {
                handleEnteredByPushNotification()
                trackPushNotificationClicked(action.data)
            }
            is MainAction.OnNotificationSet -> handleNotificationSet(action.isEnableNotification)
        }
    }

    private fun handleMainScreenEntered() = intent {
        viewModelScope.launch(coroutineDispatcher) {
            cacheProfileUseCase()
            commonRepository.getRestriction()
                .onSuccess {
                    reduce {
                        state.copy(restriction = it)
                    }
                }
        }
    }

    private fun handleNotificationSet(isEnableNotification: Boolean) = intent {
        viewModelScope.launch {
            userRepository.setFeatureNotificationSetting(isEnableNotification)
                .onFailure {
                    Timber.e(it)
                }
        }
    }

    private fun handleEnteredByPushNotification() = intent {
        reduce { state.copy(isPushNotificationNavigation = true) }
    }

    private fun handleNavigateByPushNotification() = intent {
        reduce { state.copy(isPushNotificationNavigation = false) }
    }

    private fun trackPushNotificationClicked(data: MainScreenNavArgs) {
        viewModelScope.launch(coroutineDispatcher) {
            analyticsHelper.updateUserId(data.userId)
            analyticsHelper.logEvent(
                event = AnalyticsEvent(
                    type = "push_notification_clicked",
                    extras = listOf(
                        AnalyticsEvent.Param("targetId", data.targetId.toString()),
                        AnalyticsEvent.Param("type", data.type.name),
                    )
                )
            )
        }
    }
}
