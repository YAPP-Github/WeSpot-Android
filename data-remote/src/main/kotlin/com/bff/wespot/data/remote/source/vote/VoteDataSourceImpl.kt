package com.bff.wespot.data.remote.source.vote

import com.bff.wespot.data.remote.model.vote.request.VoteResultsUploadDto
import com.bff.wespot.data.remote.model.vote.response.IndividualReceivedDto
import com.bff.wespot.data.remote.model.vote.response.VoteItemsDto
import com.bff.wespot.data.remote.model.vote.response.VoteReceivedDto
import com.bff.wespot.data.remote.model.vote.response.VoteResultsDto
import com.bff.wespot.data.remote.model.vote.response.VoteSentDto
import com.bff.wespot.network.extensions.safeRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import io.ktor.http.path
import javax.inject.Inject

class VoteDataSourceImpl @Inject constructor(
    private val httpClient: HttpClient
): VoteDataSource {
    override suspend fun getVoteQuestions(): Result<VoteItemsDto> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("api/v1/votes/options")
            }
        }

    override suspend fun uploadVoteResults(voteResults: VoteResultsUploadDto): Result<Unit> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Post
                path("api/v1/votes")
            }
            setBody(voteResults)
        }

    override suspend fun getVoteResults(date: String): Result<VoteResultsDto> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("api/v1/votes")
                parameter("date", date)
            }
        }

    override suspend fun getFirstVoteResults(date: String): Result<VoteResultsDto> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("api/v1/votes/tops")
                parameter("date", date)
            }
        }

    override suspend fun getVoteSent(cursorId: Int?): Result<VoteSentDto> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("api/v1/votes/sent")
                parameter("cursorId", cursorId)
            }
        }

    override suspend fun getVoteReceived(cursorId: Int?): Result<VoteReceivedDto> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("api/v1/votes/received")
                parameter("cursorId", cursorId)
            }
        }

    override suspend fun getReceivedVote(
        date: String,
        optionId: Int,
    ): Result<IndividualReceivedDto> =
        httpClient.safeRequest {
            url {
                method = HttpMethod.Get
                path("api/v1/votes/received/options/$optionId")
                parameter("date", date)
            }
        }
}