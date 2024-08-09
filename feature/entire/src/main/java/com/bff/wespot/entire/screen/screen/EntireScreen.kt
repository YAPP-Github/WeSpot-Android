package com.bff.wespot.entire.screen.screen

import android.graphics.Color.parseColor
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bff.wespot.designsystem.component.banner.WSBanner
import com.bff.wespot.designsystem.component.banner.WSBannerType
import com.bff.wespot.designsystem.component.header.WSTopBar
import com.bff.wespot.designsystem.theme.StaticTypeScale
import com.bff.wespot.designsystem.theme.WeSpotThemeManager
import com.bff.wespot.entire.R
import com.bff.wespot.entire.screen.component.EntireListItem
import com.bff.wespot.entire.screen.screen.edit.ProfileEditNavArgs
import com.bff.wespot.entire.screen.state.EntireAction
import com.bff.wespot.entire.screen.viewmodel.EntireViewModel
import com.bff.wespot.model.user.response.Profile
import com.ramcosta.composedestinations.annotation.Destination
import org.orbitmvi.orbit.compose.collectAsState

interface EntireNavigator {
    fun navigateUp()
    fun navigateToSetting()
    fun navigateToProfileEditScreen(args: ProfileEditNavArgs)
}

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
internal fun EntireScreen(
    navigator: EntireNavigator,
    viewModel: EntireViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val action = viewModel::onAction

    Scaffold(
        topBar = {
            WSTopBar(
                title = "",
                canNavigateBack = true,
                navigateUp = { navigator.navigateUp() },
                action = {
                    Icon(
                        modifier = Modifier
                            .clickable { navigator.navigateToSetting() }
                            .padding(end = 16.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_setting),
                        contentDescription = stringResource(R.string.setting_icon),
                        tint = WeSpotThemeManager.colors.secondaryBtnColor,
                    )
                },
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(horizontal = 20.dp),
        ) {
            ProfileContent(
                profile = state.profile,
                onClick = { navigator.navigateToProfileEditScreen(ProfileEditNavArgs(false)) },
            )

            Spacer(modifier = Modifier.height(8.dp))

            WSBanner(
                title = stringResource(R.string.vote_question_banner_title),
                subTitle = stringResource(R.string.vote_question_banner_subtitle),
                image = painterResource(id = R.drawable.vote_question_ask),
                onBannerClick = {},
                hasBorder = true,
                bannerType = WSBannerType.Primary,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp, start = 4.dp, end = 4.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                EntireListItem(text = stringResource(R.string.contact_channel)) {
                }

                EntireListItem(text = stringResource(R.string.official_sns)) {
                }

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = WeSpotThemeManager.colors.cardBackgroundColor,
                )

                EntireListItem(text = stringResource(R.string.leave_store_review)) {
                }

                EntireListItem(text = stringResource(R.string.send_feedback)) {
                }

                EntireListItem(text = stringResource(R.string.participate_in_research)) {
                }

                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 1.dp,
                    color = WeSpotThemeManager.colors.cardBackgroundColor,
                )

                EntireListItem(text = stringResource(R.string.wespot_makers)) {
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        action(EntireAction.OnEntireScreenEntered)
    }
}

@Composable
fun ProfileContent(
    profile: Profile,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(vertical = 16.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    runCatching {
                        Color(parseColor(profile.profileCharacter.backgroundColor))
                    }.getOrDefault(WeSpotThemeManager.colors.cardBackgroundColor),
                ),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(profile.profileCharacter.iconUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(com.bff.wespot.ui.R.string.user_character_image),
            )
        }

        Column(
            modifier = Modifier
                .padding(start = 12.dp, end = 8.dp)
                .weight(1f),
        ) {
            Text(
                text = profile.name,
                style = StaticTypeScale.Default.body2,
                color = WeSpotThemeManager.colors.txtTitleColor,
            )

            Text(
                text = profile.toSchoolInfo(),
                style = StaticTypeScale.Default.body6,
                color = WeSpotThemeManager.colors.txtTitleColor,
            )
        }

        FilterChip(
            shape = WeSpotThemeManager.shapes.extraLarge,
            onClick = { onClick() },
            selected = false,
            label = {
                Text(
                    text = stringResource(R.string.profile_edit),
                    style = StaticTypeScale.Default.body9,
                )
            },
            border = null,
            colors = FilterChipDefaults.filterChipColors(
                containerColor = WeSpotThemeManager.colors.secondaryBtnColor,
                labelColor = WeSpotThemeManager.colors.txtTitleColor,
            ),
        )
    }
}
