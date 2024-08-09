package com.bff.wespot.entire.screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bff.wespot.designsystem.component.indicator.WSToastType
import com.bff.wespot.domain.repository.CommonRepository
import com.bff.wespot.domain.repository.user.UserRepository
import com.bff.wespot.domain.usecase.CheckProfanityUseCase
import com.bff.wespot.entire.R
import com.bff.wespot.entire.screen.common.INPUT_DEBOUNCE_TIME
import com.bff.wespot.entire.screen.common.INTRODUCTION_MAX_LENGTH
import com.bff.wespot.entire.screen.state.edit.EntireEditAction
import com.bff.wespot.entire.screen.state.edit.EntireEditSideEffect
import com.bff.wespot.entire.screen.state.edit.EntireEditUiState
import com.bff.wespot.model.ToastState
import com.bff.wespot.model.common.BackgroundColor
import com.bff.wespot.model.common.Character
import com.bff.wespot.model.user.response.ProfileCharacter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EntireEditViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val commonRepository: CommonRepository,
    private val checkProfanityUseCase: CheckProfanityUseCase,
) : ViewModel(), ContainerHost<EntireEditUiState, EntireEditSideEffect> {
    override val container = container<EntireEditUiState, EntireEditSideEffect>(EntireEditUiState())

    private val introductionInput: MutableStateFlow<String> = MutableStateFlow("")

    val characters: StateFlow<List<Character>> = flow {
        commonRepository.getCharacters().onSuccess { emit(it) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )

    val backgroundColor: StateFlow<List<BackgroundColor>> = flow {
        commonRepository.getBackgroundColors().onSuccess { emit(it) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList(),
    )

    fun onAction(action: EntireEditAction) {
        when (action) {
            EntireEditAction.OnCharacterEditScreenEntered -> getProfile()
            EntireEditAction.OnIntroductionEditDoneButtonClicked -> updateIntroduction()
            is EntireEditAction.OnProfileEditScreenEntered -> {
                getProfile()
                observeIntroductionInput()
                if (action.isCompleteEdit) {
                    postEditDoneSideToast()
                }
            }
            is EntireEditAction.OnProfileEditTextFieldFocused ->
                handleProfileEditButtonText(action.focused)
            is EntireEditAction.OnIntroductionChanged -> handleIntroductionChanged(action.introduction)
            is EntireEditAction.OnCharacterEditDoneButtonClicked -> updateCharacter(action.character)
        }
    }

    private fun getProfile() = intent {
        viewModelScope.launch {
            userRepository.getProfile()
                .onSuccess { profile ->
                    reduce { state.copy(profile = profile) }
                    handleIntroductionChanged(profile.introduction)
                }
                .onFailure {
                    Timber.e(it)
                }
        }
    }

    private fun handleIntroductionChanged(introduction: String) = intent {
        reduce {
            introductionInput.value = introduction
            state.copy(introductionInput = introduction)
        }
    }

    private fun handleProfileEditButtonText(focused: Boolean) = intent {
        reduce {
            state.copy(isIntroductionEditing = focused)
        }
    }

    private fun observeIntroductionInput() {
        viewModelScope.launch {
            introductionInput
                .debounce(INPUT_DEBOUNCE_TIME)
                .distinctUntilChanged()
                .collect { introduction ->
                    if (introduction.length in 1..INTRODUCTION_MAX_LENGTH) {
                        hasProfanity(introduction)
                    }
                }
        }
    }

    private fun postEditDoneSideToast() = intent {
        postSideEffect(
            EntireEditSideEffect.ShowToast(
                ToastState(
                    show = true,
                    message = R.string.edit_done,
                    type = WSToastType.Success,
                ),
            ),
        )
    }

    private fun hasProfanity(introduction: String) = intent {
        runCatching {
            val result = checkProfanityUseCase(introduction)
            reduce {
                state.copy(
                    hasProfanity = result,
                )
            }
        }
    }

    private fun updateIntroduction() = intent {
        reduce { state.copy(isLoading = true) }
        viewModelScope.launch {
            userRepository.updateIntroduction(state.introductionInput)
                .onSuccess {
                    postEditDoneSideToast()
                    reduce { state.copy(isLoading = false) }
                }
                .onFailure {
                    Timber.e(it)
                    reduce { state.copy(isLoading = false) }
                }
        }
    }

    private fun updateCharacter(character: ProfileCharacter) = intent {
        reduce { state.copy(isLoading = true) }
        viewModelScope.launch {
            userRepository.updateCharacter(character = character)
                .onSuccess {
                    postSideEffect(EntireEditSideEffect.NavigateToEntire)
                    reduce { state.copy(isLoading = false) }
                }
                .onFailure {
                    Timber.e(it)
                    reduce { state.copy(isLoading = false) }
                }
        }
    }
}
