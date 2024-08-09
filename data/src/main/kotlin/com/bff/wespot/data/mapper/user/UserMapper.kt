package com.bff.wespot.data.mapper.user

import com.bff.wespot.data.remote.model.user.response.ProfileCharacterDto
import com.bff.wespot.model.user.response.ProfileCharacter

internal fun ProfileCharacter.toProfileCharacterDto() = ProfileCharacterDto(
    iconUrl = iconUrl,
    backgroundColor = backgroundColor,
)
