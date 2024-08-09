package com.bff.wespot.data.mapper.vote

import com.bff.wespot.data.remote.model.vote.request.VoteResultUploadDto
import com.bff.wespot.data.remote.model.vote.request.VoteResultsUploadDto
import com.bff.wespot.model.vote.request.VoteResultUpload
import com.bff.wespot.model.vote.request.VoteResultsUpload

fun VoteResultsUpload.toDto() =
    VoteResultsUploadDto(
        voteRequests = voteResults.map { it.toDto() }
    )

fun VoteResultUpload.toDto() =
    VoteResultUploadDto(
        userId = userId,
        voteOptionId = voteOptionId
    )