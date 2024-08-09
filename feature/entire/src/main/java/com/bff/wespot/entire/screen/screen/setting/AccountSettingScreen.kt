package com.bff.wespot.entire.screen.screen.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bff.wespot.designsystem.component.header.WSTopBar
import com.bff.wespot.designsystem.component.modal.WSDialog
import com.bff.wespot.designsystem.theme.StaticTypeScale
import com.bff.wespot.designsystem.theme.WeSpotThemeManager
import com.bff.wespot.entire.R
import com.bff.wespot.entire.screen.state.EntireAction
import com.bff.wespot.entire.screen.state.EntireSideEffect
import com.bff.wespot.entire.screen.viewmodel.EntireViewModel
import com.bff.wespot.navigation.Navigator
import com.ramcosta.composedestinations.annotation.Destination
import org.orbitmvi.orbit.compose.collectSideEffect

interface AccountSettingNavigator {
    fun navigateUp()
    fun navigateToRevokeScreen()
}

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun AccountSettingScreen(
    navigator: AccountSettingNavigator,
    activityNavigator: Navigator,
    viewModel: EntireViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    val action = viewModel::onAction

    viewModel.collectSideEffect {
        when (it) {
            is EntireSideEffect.NavigateToAuth -> {
                val intent = activityNavigator.navigateToAuth(context)
                context.startActivity(intent)
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            WSTopBar(
                title = stringResource(id = R.string.account_setting),
                canNavigateBack = true,
                navigateUp = { navigator.navigateUp() },
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(top = 16.dp, start = 24.dp, end = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            AccountSettingItem(title = stringResource(R.string.sign_out)) {
                showDialog = true
            }

            AccountSettingItem(title = stringResource(R.string.user_revoke)) {
                navigator.navigateToRevokeScreen()
            }
        }
    }

    if (showDialog) {
        WSDialog(
            title = stringResource(R.string.sign_out_dialog_title),
            subTitle = "",
            okButtonText = stringResource(R.string.close),
            cancelButtonText = stringResource(id = R.string.sign_out),
            okButtonClick = { showDialog = false },
            cancelButtonClick = { action(EntireAction.OnSignOutButtonClicked) },
            onDismissRequest = { },
        )
    }
}

@Composable
fun AccountSettingItem(title: String, onClick: () -> Unit) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        text = title,
        style = StaticTypeScale.Default.body4,
        color = WeSpotThemeManager.colors.txtTitleColor,
    )
}
