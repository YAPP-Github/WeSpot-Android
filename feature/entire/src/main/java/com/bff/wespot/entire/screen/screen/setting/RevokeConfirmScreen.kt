package com.bff.wespot.entire.screen.screen.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bff.wespot.designsystem.component.button.WSButton
import com.bff.wespot.designsystem.component.button.WSButtonType
import com.bff.wespot.designsystem.component.header.WSTopBar
import com.bff.wespot.designsystem.component.modal.WSDialog
import com.bff.wespot.designsystem.theme.StaticTypeScale
import com.bff.wespot.designsystem.theme.WeSpotThemeManager
import com.bff.wespot.entire.R
import com.bff.wespot.entire.screen.state.EntireAction
import com.bff.wespot.entire.screen.state.EntireSideEffect
import com.bff.wespot.entire.screen.viewmodel.EntireViewModel
import com.bff.wespot.navigation.Navigator
import com.bff.wespot.navigation.util.EXTRA_TOAST_MESSAGE
import com.bff.wespot.ui.WSBottomSheet
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.collections.immutable.persistentListOf
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

interface RevokeConfirmNavigator {
    fun navigateUp()
}

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun RevokeConfirmScreen(
    navigator: RevokeConfirmNavigator,
    activityNavigator: Navigator,
    viewModel: EntireViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val action = viewModel::onAction
    val state by viewModel.collectAsState()

    viewModel.collectSideEffect {
        when (it) {
            is EntireSideEffect.NavigateToAuth -> {
                val intent = activityNavigator.navigateToAuth(context)
                intent.putExtra(EXTRA_TOAST_MESSAGE, context.getString(R.string.revoke_done))
                context.startActivity(intent)
            }
            else -> {}
        }
    }

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
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                modifier = Modifier.padding(bottom = 16.dp, start = 24.dp, end = 24.dp),
                text = stringResource(R.string.revoke_confirm_title),
                style = StaticTypeScale.Default.header1,
                color = WeSpotThemeManager.colors.txtTitleColor,
            )

            persistentListOf(
                stringResource(R.string.feature_variety_lacking),
                stringResource(R.string.lacking_friends),
                stringResource(R.string.choices_variety_lacking),
                stringResource(R.string.difficult_to_use),
                stringResource(R.string.other),
            ).forEach { reason ->
                RevokeReasonItem(
                    title = reason,
                    selected = reason in state.revokeReasonList,
                    onClick = {
                        action(EntireAction.OnRevokeReasonSelected(reason))
                    },
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            WSButton(
                text = stringResource(R.string.select_done),
                buttonType = WSButtonType.Primary,
                enabled = state.revokeReasonList.isNotEmpty(),
                content = { it() },
                onClick = { showBottomSheet = true },
            )
        }
    }

    if (showBottomSheet) {
        WSBottomSheet(closeSheet = { showBottomSheet = false }) {
            RevokeBottomSheetContent(
                revokeConfirmed = state.revokeConfirmed,
                onButtonClicked = {
                    showBottomSheet = false
                    showDialog = true
                },
                onRevokeConfirmed = { action(EntireAction.OnRevokeConfirmed) },
            )
        }
    }

    if (showDialog) {
        WSDialog(
            title = stringResource(R.string.revoke_dialog_title),
            subTitle = "",
            okButtonText = stringResource(R.string.close),
            cancelButtonText = stringResource(id = R.string.revoke),
            okButtonClick = { showDialog = false },
            cancelButtonClick = {
                action(EntireAction.OnRevokeButtonClicked)
            },
            onDismissRequest = { },
        )
    }
}

@Composable
fun RevokeReasonItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit = { },
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 20.dp)
            .clip(WeSpotThemeManager.shapes.medium)
            .border(
                width = 1.dp,
                color = if (selected) {
                    WeSpotThemeManager.colors.primaryColor
                } else {
                    WeSpotThemeManager.colors.cardBackgroundColor
                },
                shape = WeSpotThemeManager.shapes.medium,
            )
            .background(WeSpotThemeManager.colors.cardBackgroundColor)
            .clickable { onClick.invoke() },
    ) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.padding(start = 8.dp),
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = stringResource(R.string.check_icon),
                tint = if (selected) {
                    WeSpotThemeManager.colors.primaryColor
                } else {
                    WeSpotThemeManager.colors.disableIcnColor
                },
            )

            Text(
                text = title,
                style = StaticTypeScale.Default.body4,
                color = WeSpotThemeManager.colors.txtTitleColor,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun RevokeBottomSheetContent(
    revokeConfirmed: Boolean,
    onButtonClicked: () -> Unit,
    onRevokeConfirmed: () -> Unit,
) {
    Column(
        modifier = Modifier.padding(top = 28.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            text = stringResource(R.string.revoke_bottom_sheet_title),
            style = StaticTypeScale.Default.body1,
            color = WeSpotThemeManager.colors.txtTitleColor,
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 20.dp, end = 20.dp),
            text = stringResource(R.string.revoke_bottom_sheet_content),
            style = StaticTypeScale.Default.body6,
            color = WeSpotThemeManager.colors.txtSubColor,
            maxLines = 2,
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp, start = 20.dp, end = 20.dp),
            text = stringResource(R.string.revoke_bottom_sheet_content2),
            style = StaticTypeScale.Default.body6,
            color = WeSpotThemeManager.colors.txtSubColor,
            maxLines = 2,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 19.dp, bottom = 12.dp, start = 20.dp, end = 20.dp)
                .clickable { onRevokeConfirmed() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = stringResource(R.string.check_icon),
                tint = if (revokeConfirmed) {
                    WeSpotThemeManager.colors.primaryColor
                } else {
                    WeSpotThemeManager.colors.disableIcnColor
                },
            )

            Text(
                text = stringResource(R.string.revoke_confirm_text),
                style = StaticTypeScale.Default.body5,
                color = WeSpotThemeManager.colors.txtTitleColor,
            )
        }

        WSButton(
            text = stringResource(R.string.do_revoke),
            onClick = { onButtonClicked() },
            enabled = revokeConfirmed,
            content = { it() },
        )
    }
}
