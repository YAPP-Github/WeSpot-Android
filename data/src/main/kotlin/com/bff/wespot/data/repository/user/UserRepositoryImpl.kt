package com.bff.wespot.data.repository.user

import com.bff.wespot.data.mapper.user.toNotificationSettingDto
import com.bff.wespot.data.mapper.user.toProfileCharacterDto
import com.bff.wespot.data.remote.model.user.request.IntroductionDto
import com.bff.wespot.data.remote.source.user.UserDataSource
import com.bff.wespot.domain.repository.user.UserRepository
import com.bff.wespot.model.user.response.NotificationSetting
import com.bff.wespot.model.user.response.Profile
import com.bff.wespot.model.user.response.ProfileCharacter
import com.bff.wespot.model.user.response.User
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource,
) : UserRepository {
    override suspend fun getProfile(): Result<Profile> =
        userDataSource.getProfile().map { profileDto ->
            profileDto.toProfile()
        }

    override suspend fun getNotificationSetting(): Result<NotificationSetting> =
        userDataSource.getNotificationSetting().mapCatching { it.toNotificationSetting() }

    override suspend fun updateNotificationSetting(
        notificationSetting: NotificationSetting,
    ): Result<Unit> =
        userDataSource.updateNotificationSetting(notificationSetting.toNotificationSettingDto())

    override suspend fun updateIntroduction(introduction: String): Result<Unit> =
        userDataSource.updateIntroduction(introduction = IntroductionDto(introduction))

    override suspend fun updateCharacter(character: ProfileCharacter): Result<Unit> =
        userDataSource.updateCharacter(character.toProfileCharacterDto())
}
