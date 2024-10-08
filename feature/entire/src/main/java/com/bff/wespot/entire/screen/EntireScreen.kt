package com.bff.wespot.entire.screen

import android.graphics.Color.parseColor
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bff.wespot.designsystem.component.banner.WSBanner
import com.bff.wespot.designsystem.component.banner.WSBannerType
import com.bff.wespot.designsystem.theme.StaticTypeScale
import com.bff.wespot.designsystem.theme.WeSpotThemeManager
import com.bff.wespot.domain.util.RemoteConfigKey
import com.bff.wespot.entire.R
import com.bff.wespot.entire.component.EntireListItem
import com.bff.wespot.entire.screen.edit.ProfileEditNavArgs
import com.bff.wespot.entire.state.EntireAction
import com.bff.wespot.entire.viewmodel.EntireViewModel
import com.bff.wespot.model.user.response.Profile
import com.bff.wespot.navigation.Navigator
import com.ramcosta.composedestinations.annotation.Destination
import org.orbitmvi.orbit.compose.collectAsState

interface EntireNavigator {
    fun navigateToSetting()
    fun navigateToProfileEditScreen(args: ProfileEditNavArgs)
}

@Destination
@Composable
internal fun EntireScreen(
    navigator: EntireNavigator,
    activityNavigator: Navigator,
    viewModel: EntireViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val state by viewModel.collectAsState()
    val action = viewModel::onAction

    Scaffold {
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
                onBannerClick = {
                    activityNavigator.navigateToWebLink(
                        context = context,
                        webLink = state.webLinkMap.getOrDefault(
                            RemoteConfigKey.VOTE_QUESTION_GOOGLE_FORM_URL,
                            context.getString(R.string.vote_question_google_form_url),
                        ),
                    )
                },
                hasBorder = true,
                bannerType = WSBannerType.Primary,
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp),
            ) {
                EntireListItem(text = stringResource(R.string.contact_channel)) {
                    activityNavigator.navigateToWebLink(
                        context = context,
                        webLink = state.webLinkMap.getOrDefault(
                            RemoteConfigKey.WESPOT_KAKAO_CHANNEL_URL,
                            context.getString(R.string.wespot_kakao_channel_url),
                        ),
                    )
                }

                EntireListItem(text = stringResource(R.string.official_sns)) {
                    activityNavigator.navigateToWebLink(
                        context = context,
                        webLink = state.webLinkMap.getOrDefault(
                            RemoteConfigKey.WESPOT_INSTAGRAM_URL,
                            context.getString(R.string.wespot_instagram_url),
                        ),
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 4.dp),
                    thickness = 1.dp,
                    color = WeSpotThemeManager.colors.cardBackgroundColor,
                )

                EntireListItem(text = stringResource(R.string.leave_store_review)) {
                    activityNavigator.navigateToWebLink(
                        context = context,
                        webLink = state.webLinkMap.getOrDefault(
                            RemoteConfigKey.PLAY_STORE_URL,
                            context.getString(R.string.play_store_url),
                        ),
                    )
                }

                EntireListItem(text = stringResource(R.string.send_feedback)) {
                    activityNavigator.navigateToWebLink(
                        context = context,
                        webLink = state.webLinkMap.getOrDefault(
                            RemoteConfigKey.USER_OPINION_GOOGLE_FORM_URL,
                            context.getString(R.string.user_opinion_google_form_url),
                        ),
                    )
                }

                EntireListItem(text = stringResource(R.string.participate_in_research)) {
                    activityNavigator.navigateToWebLink(
                        context = context,
                        webLink = state.webLinkMap.getOrDefault(
                            RemoteConfigKey.RESEARCH_PARTICIPATION_GOOGLE_FORM_URL,
                            context.getString(R.string.research_participation_google_form_url),
                        ),
                    )
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 4.dp),
                    thickness = 1.dp,
                    color = WeSpotThemeManager.colors.cardBackgroundColor,
                )

                EntireListItem(text = stringResource(R.string.wespot_makers)) {
                    activityNavigator.navigateToWebLink(
                        context = context,
                        webLink = state.webLinkMap.getOrDefault(
                            RemoteConfigKey.WESPOT_MAKERS_URL,
                            context.getString(R.string.wespot_makers_url),
                        ),
                    )
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

        Box(
            modifier = Modifier
                .clip(WeSpotThemeManager.shapes.extraLarge)
                .clickable { onClick() }
                .background(WeSpotThemeManager.colors.secondaryBtnColor),
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                text = stringResource(R.string.profile_edit),
                style = StaticTypeScale.Default.body9,
                color = Color(0xFFF7F7F8),
            )
        }
    }
}
