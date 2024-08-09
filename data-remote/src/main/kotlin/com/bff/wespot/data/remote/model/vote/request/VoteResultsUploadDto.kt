package com.bff.wespot.data.remote.model.vote.request

import com.bff.wespot.model.vote.request.VoteResultsUpload
import kotlinx.serialization.Serializable

@Serializable
data class VoteResultsUploadDto(
    val voteRequests: List<VoteResultUploadDto>
) {
    fun Result() = VoteResultsUpload(
        voteResults = voteRequests.map { it.toVote() }
    )
}