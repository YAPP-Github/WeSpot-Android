package com.bff.wespot.data.remote.model.vote.response

import com.bff.wespot.model.vote.response.Result
import com.bff.wespot.model.vote.response.VoteResult
import com.bff.wespot.model.vote.response.VoteResults
import kotlinx.serialization.Serializable

@Serializable
data class VoteResultsDto(
    val voteResults: List<VoteResultDto>
) {
    fun toVoteResults() = VoteResults(
        voteResults = voteResults.map { it.toVoteResult() }
    )
}

@Serializable
data class VoteResultDto(
    val voteOption: VoteOptionDto,
    val voteResult: List<ResultDto>,
) {
    fun toVoteResult() = VoteResult(
        voteOption = voteOption.toVoteOption(),
        results = voteResult.map { it.toResult() },
    )
}

@Serializable
data class ResultDto(
    val user: VoteUserDto,
    val voteCount: Int,
) {
    fun toResult() = Result(
        user = user.toVoteUser(),
        voteCount = voteCount,
    )
}
