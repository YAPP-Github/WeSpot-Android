package com.bff.wespot.entire.screen.screen.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bff.wespot.designsystem.component.button.WSButton
import com.bff.wespot.designsystem.component.button.WSButtonType
import com.bff.wespot.designsystem.component.header.WSTopBar
import com.bff.wespot.designsystem.theme.StaticTypeScale
import com.bff.wespot.designsystem.theme.WeSpotThemeManager
import com.bff.wespot.entire.R
import com.bff.wespot.entire.screen.state.EntireAction
import com.bff.wespot.entire.screen.viewmodel.EntireViewModel
import com.ramcosta.composedestinations.annotation.Destination
import org.orbitmvi.orbit.compose.collectAsState

interface RevokeNavigator {
    fun navigateUp()
    fun navigateToRevokeConfirmScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun RevokeScreen(
    navigator: RevokeNavigator,
    viewModel: EntireViewModel = hiltViewModel(),
) {
    val action = viewModel::onAction
    val state by viewModel.collectAsState()

    Scaffold(
        topBar = {
            WSTopBar(
                title = stringResource(id = R.string.user_revoke),
                canNavigateBack = true,
                navigateUp = { navigator.navigateUp() },
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(it),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text(
                modifier = Modifier.padding(bottom = 16.dp, start = 24.dp, end = 24.dp),
                text = stringResource(R.string.revoke_screen_title, state.profile.name),
                color = WeSpotThemeManager.colors.txtTitleColor,
                style = StaticTypeScale.Default.header1,
            )

            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(com.bff.wespot.designsystem.R.drawable.error),
                        contentDescription = stringResource(R.string.error_icon),
                    )

                    Text(
                        text = stringResource(R.string.notify_list),
                        color = WeSpotThemeManager.colors.txtTitleColor,
                        style = StaticTypeScale.Default.body3,
                    )
                }

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.revoke_warning_content),
                    color = WeSpotThemeManager.colors.txtTitleColor,
                    style = StaticTypeScale.Default.body6,
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                text = stringResource(R.string.revoke_message_cancel_title),
                color = WeSpotThemeManager.colors.txtSubColor,
                style = StaticTypeScale.Default.body8,
            )

            Spacer(modifier = Modifier.weight(1f))

            WSButton(
                text = stringResource(R.string.leave_memory),
                buttonType = WSButtonType.Primary,
                content = { it() },
                onClick = {
                    navigator.navigateToRevokeConfirmScreen()
                },
            )
        }
    }

    LaunchedEffect(Unit) {
        action(EntireAction.OnRevokeScreenEntered)
    }
}
