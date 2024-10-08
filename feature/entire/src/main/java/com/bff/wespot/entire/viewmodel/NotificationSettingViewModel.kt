package com.bff.wespot.entire.viewmodel

import androidx.lifecycle.viewModelScope
import com.bff.wespot.common.extension.onNetworkFailure
import com.bff.wespot.domain.repository.user.UserRepository
import com.bff.wespot.entire.screen.state.notification.NotificationSettingAction
import com.bff.wespot.entire.screen.state.notification.NotificationSettingSideEffect
import com.bff.wespot.entire.screen.state.notification.NotificationSettingUiState
import com.bff.wespot.model.user.response.NotificationSetting
import com.bff.wespot.ui.base.BaseViewModel
import com.bff.wespot.ui.model.SideEffect.Companion.toSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class NotificationSettingViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : BaseViewModel(), ContainerHost<NotificationSettingUiState, NotificationSettingSideEffect> {
    override val container = container<NotificationSettingUiState, NotificationSettingSideEffect>(
        NotificationSettingUiState(),
    )

    fun onAction(action: NotificationSettingAction) {
        when (action) {
            NotificationSettingAction.OnNotificationSettingScreenEntered -> {
                handleScreenEntered()
            }
            is NotificationSettingAction.OnVoteNotificationSwitched -> {
                handleVoteNotificationSwitched()
            }
            is NotificationSettingAction.OnMessageNotificationSwitched -> {
                handleMessageNotificationSwitched()
            }
            is NotificationSettingAction.OnMarketingNotificationSwitched -> {
                handleMarketingNotificationSwitched()
            }
            NotificationSettingAction.OnNotificationSettingScreenExited -> {
                postNotificationSetting()
            }
            NotificationSettingAction.SetMarketingNotificationEnable -> {
                setMarketingNotification(true)
            }
        }
    }

    private fun handleScreenEntered() = intent {
        if (state.hasScreenBeenEntered) {
            return@intent
        }
        reduce {
            state.copy(isLoading = true, hasScreenBeenEntered = true)
        }

        viewModelScope.launch {
            userRepository.getNotificationSetting()
                .onSuccess { setting ->
                    reduce {
                        state.copy(
                            isLoading = false,
                            initialNotificationSetting = setting,
                            isEnableVoteNotification = setting.isEnableVoteNotification,
                            isEnableMessageNotification = setting.isEnableMessageNotification,
                            isEnableMarketingNotification = setting.isEnableMarketingNotification,
                        )
                    }
                }
                .onNetworkFailure {
                    postSideEffect(it.toSideEffect())
                }
                .onFailure {
                    reduce { state.copy(isLoading = false) }
                }
        }
    }

    private fun handleVoteNotificationSwitched() = intent {
        reduce { state.copy(isEnableVoteNotification = state.isEnableVoteNotification.not()) }
    }

    private fun handleMessageNotificationSwitched() = intent {
        reduce { state.copy(isEnableMessageNotification = state.isEnableMessageNotification.not()) }
    }

    private fun handleMarketingNotificationSwitched() = intent {
        // 변경되는 상태를 기준으로 SideEffect 방출
        when (state.isEnableMarketingNotification.not()) {
            true -> {
                postSideEffect(NotificationSettingSideEffect.ShowMarketingDialog)
            }

            false -> {
                postSideEffect(NotificationSettingSideEffect.ShowMarketingResultDialog)
                setMarketingNotification(false)
            }
        }
    }

    private fun setMarketingNotification(isEnable: Boolean) = intent {
        reduce {
            state.copy(isEnableMarketingNotification = isEnable)
        }
    }

    private fun postNotificationSetting() = intent {
        val updatedNotificationSetting = NotificationSetting(
            isEnableVoteNotification = state.isEnableVoteNotification,
            isEnableMessageNotification = state.isEnableMessageNotification,
            isEnableMarketingNotification = state.isEnableMarketingNotification,
        )

        if (updatedNotificationSetting != state.initialNotificationSetting) {
            viewModelScope.launch {
                userRepository.updateNotificationSetting(updatedNotificationSetting)
                    .onFailure {
                        Timber.e(it)
                    }
            }
        }
    }
}
