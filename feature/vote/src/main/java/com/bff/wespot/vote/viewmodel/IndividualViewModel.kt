package com.bff.wespot.vote.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bff.wespot.common.extension.onNetworkFailure
import com.bff.wespot.common.util.toDateString
import com.bff.wespot.domain.repository.vote.VoteRepository
import com.bff.wespot.ui.base.BaseViewModel
import com.bff.wespot.ui.model.SideEffect.Companion.toSideEffect
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class IndividualViewModel @Inject constructor(
    private val voteRepository: VoteRepository,
    private val savedStateHandle: SavedStateHandle,
) : BaseViewModel() {
    val individual = flow {
        val date = savedStateHandle["date"] ?: LocalDate.now().toDateString()
        val optionId = savedStateHandle["optionId"] ?: 0
        voteRepository.getReceivedVote(date, optionId)
            .onSuccess {
                emit(it)
            }
            .onNetworkFailure {
                postSideEffect(it.toSideEffect())
            }
            .onFailure {
                Timber.e(it)
            }
    }.stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(5000), null)
}
