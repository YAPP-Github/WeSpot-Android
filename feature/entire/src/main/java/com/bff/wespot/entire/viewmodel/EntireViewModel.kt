package com.bff.wespot.entire.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.bff.wespot.common.extension.onNetworkFailure
import com.bff.wespot.domain.repository.BasePagingRepository
import com.bff.wespot.domain.repository.DataStoreRepository
import com.bff.wespot.domain.repository.auth.AuthRepository
import com.bff.wespot.domain.repository.firebase.config.RemoteConfigRepository
import com.bff.wespot.domain.repository.message.MessageStorageRepository
import com.bff.wespot.domain.repository.user.ProfileRepository
import com.bff.wespot.domain.util.RemoteConfigKey
import com.bff.wespot.entire.state.EntireAction
import com.bff.wespot.entire.state.EntireSideEffect
import com.bff.wespot.entire.state.EntireUiState
import com.bff.wespot.model.common.Paging
import com.bff.wespot.model.message.response.BlockedMessage
import com.bff.wespot.ui.base.BaseViewModel
import com.bff.wespot.ui.model.SideEffect.Companion.toSideEffect
import com.bff.wespot.ui.util.KakaoLoginManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EntireViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
    private val remoteConfigRepository: RemoteConfigRepository,
    private val messageStorageRepository: MessageStorageRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val messageBlockedRepository: BasePagingRepository<BlockedMessage, Paging<BlockedMessage>>,
) : BaseViewModel(), ContainerHost<EntireUiState, EntireSideEffect> {
    override val container = container<EntireUiState, EntireSideEffect>(EntireUiState())

    fun onAction(action: EntireAction) {
        when (action) {
            EntireAction.OnEntireScreenEntered -> {
                observeProfileDataFlow()
                fetchWebLinkFromRemoteConfig()
            }
            EntireAction.OnSettingScreenEntered -> fetchWebLinkFromRemoteConfig()
            EntireAction.OnRevokeScreenEntered -> observeProfileDataFlow()
            EntireAction.OnBlockListScreenEntered -> getBlockedMessageList()
            EntireAction.OnRevokeConfirmed -> handleRevokeConfirmed()
            EntireAction.OnRevokeButtonClicked -> revokeUser()
            EntireAction.OnSignOutButtonClicked -> signOut()
            EntireAction.UnBlockMessage -> unblockMessage()
            is EntireAction.OnUnBlockButtonClicked -> handleUnBlockButtonClicked(action.messageId)
            is EntireAction.OnRevokeReasonSelected -> handleRevokeReasonSelected(action.reason)
        }
    }

    private fun observeProfileDataFlow() = intent {
        viewModelScope.launch {
            profileRepository.profileDataFlow
                .distinctUntilChanged()
                .catch { exception ->
                    Timber.e(exception)
                }
                .collect {
                    reduce { state.copy(profile = it) }
                }
        }
    }

    private fun fetchWebLinkFromRemoteConfig() = intent {
        val urlList = listOf(
            RemoteConfigKey.VOTE_QUESTION_GOOGLE_FORM_URL,
            RemoteConfigKey.WESPOT_KAKAO_CHANNEL_URL,
            RemoteConfigKey.WESPOT_INSTAGRAM_URL,
            RemoteConfigKey.USER_OPINION_GOOGLE_FORM_URL,
            RemoteConfigKey.RESEARCH_PARTICIPATION_GOOGLE_FORM_URL,
            RemoteConfigKey.WESPOT_MAKERS_URL,
            RemoteConfigKey.PROFILE_CHANGE_GOOGLE_FORM_URL,
            RemoteConfigKey.PRIVACY_POLICY_URL,
            RemoteConfigKey.PLAY_STORE_URL,
            RemoteConfigKey.TERMS_OF_SERVICE_URL,
        )
        val webLinkMap = urlList.associateWith { webLink ->
            remoteConfigRepository.fetchFromRemoteConfig(webLink)
        }
        reduce { state.copy(webLinkMap = webLinkMap) }
    }

    private fun revokeUser() = intent {
        viewModelScope.launch {
            launch {
                authRepository.revoke(state.revokeReasonList)
                    .onSuccess {
                        clearCachedData()
                        postSideEffect(EntireSideEffect.NavigateToAuth)
                    }
                    .onNetworkFailure {
                        postSideEffect(it.toSideEffect())
                    }
                    .onFailure {
                        Timber.e(it)
                    }
            }
        }
    }

    private fun signOut() = intent {
        clearCachedData()
        KakaoLoginManager.logout()
        postSideEffect(EntireSideEffect.NavigateToAuth)
    }

    private fun clearCachedData() {
        viewModelScope.launch {
            launch { dataStoreRepository.clear() }
            launch { profileRepository.clearProfile() }
        }
    }

    private fun getBlockedMessageList() = intent {
        viewModelScope.launch(coroutineDispatcher) {
            runCatching {
                val result = messageBlockedRepository.fetchResultStream()
                    .cachedIn(viewModelScope)
                reduce { state.copy(blockedMessageList = result) }
            }
        }
    }

    private fun handleUnBlockButtonClicked(messageId: Int) = intent {
        reduce { state.copy(unBlockMessageId = messageId) }
    }

    private fun unblockMessage() = intent {
        reduce { state.copy(isLoading = true) }

        viewModelScope.launch {
            messageStorageRepository.unBlockMessage(state.unBlockMessageId)
                .onSuccess {
                    // 해제 완료 버튼 토글을 위해, 차단 해제된 리스트에 추가
                    if (state.unBlockList.contains(state.unBlockMessageId).not()) {
                        val updatedList = state.unBlockList.toMutableList().apply {
                            add(state.unBlockMessageId)
                        }
                        reduce { state.copy(unBlockList = updatedList, isLoading = false) }
                    }
                }
                .onFailure {
                    reduce { state.copy(isLoading = false) }
                    Timber.e(it)
                }
        }
    }

    private fun handleRevokeReasonSelected(reason: String) = intent {
        reduce {
            val updatedList = state.revokeReasonList.toMutableList().apply {
                if (contains(reason)) {
                    remove(reason)
                } else {
                    add(reason)
                }
            }
            state.copy(revokeReasonList = updatedList)
        }
    }

    private fun handleRevokeConfirmed() = intent {
        reduce {
            state.copy(revokeConfirmed = state.revokeConfirmed.not())
        }
    }
}
