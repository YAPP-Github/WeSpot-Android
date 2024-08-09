package com.bff.wespot.domain.repository.user

import com.bff.wespot.model.user.response.Profile
import com.bff.wespot.model.user.response.ProfileCharacter
import com.bff.wespot.model.user.response.User

interface UserRepository {
    suspend fun getUserListByName(name: String, cursorId: Int): Result<List<User>>

    suspend fun getProfile(): Result<Profile>

    suspend fun updateIntroduction(introduction: String): Result<Unit>

    suspend fun updateCharacter(character: ProfileCharacter): Result<Unit>
}
