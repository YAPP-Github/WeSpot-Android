package com.bff.wespot.data.remote.source.message

import com.bff.wespot.data.remote.model.message.response.ReservedMessageListDto

interface MessageStorageDataSource {
    suspend fun updateMessageReadStatus(messageId: Int): Result<Unit>

    suspend fun deleteMessage(messageId: Int): Result<Unit>

    suspend fun blockMessage(messageId: Int): Result<Unit>

    suspend fun unBlockMessage(messageId: Int): Result<Unit>

    suspend fun getReservedMessage(): Result<ReservedMessageListDto>
}
