package com.bff.wespot.data.remote.model.message.response

import com.bff.wespot.model.message.response.MessageList
import kotlinx.serialization.Serializable

@Serializable
data class MessageListDto (
    val messages: List<MessageDto>,
    val hasNext: Boolean,
) {
    fun toMessageList(): MessageList = MessageList(
        messages = messages.map { it.toMessage() },
        hasNext = hasNext,
    )
}
