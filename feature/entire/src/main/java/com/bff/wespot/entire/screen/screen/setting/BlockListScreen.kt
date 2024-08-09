package com.bff.wespot.entire.screen.screen.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bff.wespot.designsystem.component.header.WSTopBar
import com.bff.wespot.designsystem.component.modal.WSDialog
import com.bff.wespot.designsystem.theme.StaticTypeScale
import com.bff.wespot.designsystem.theme.WeSpotThemeManager
import com.bff.wespot.entire.R
import com.bff.wespot.entire.screen.state.EntireAction
import com.bff.wespot.entire.screen.viewmodel.EntireViewModel
import com.bff.wespot.ui.ReservedMessageItem
import com.ramcosta.composedestinations.annotation.Destination
import org.orbitmvi.orbit.compose.collectAsState

interface BlockListNavigator {
    fun navigateUp()
}

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun BlockListScreen(
    navigator: BlockListNavigator,
    viewModel: EntireViewModel = hiltViewModel(),
) {
    var showDialog by remember { mutableStateOf(false) }

    val action = viewModel::onAction
    val state by viewModel.collectAsState()

    Scaffold(
        topBar = {
            WSTopBar(
                title = "",
                canNavigateBack = true,
                navigateUp = { navigator.navigateUp() },
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(it),
        ) {
            Text(
                modifier = Modifier.padding(bottom = 16.dp, start = 24.dp, end = 24.dp),
                text = stringResource(R.string.block_list),
                style = StaticTypeScale.Default.header1,
                color = WeSpotThemeManager.colors.txtTitleColor,
            )

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(state.blockedMessageList, key = { message -> message.id }) { item ->
                    ReservedMessageItem(
                        title = stringResource(com.bff.wespot.designsystem.R.string.letter_sender),
                        subTitle = item.senderName,
                        backgroundColor = item.senderProfile.backgroundColor,
                        iconUrl = item.senderProfile.iconUrl,
                        chipText = stringResource(R.string.unblock),
                        chipEnabled = item.id !in state.unBlockList,
                        chipDisabledText = stringResource(R.string.unblock_done),
                        onClick = {
                            action(EntireAction.OnUnBlockButtonClicked(item.id))
                            showDialog = true
                        },
                    )
                }
            }
        }

        if (showDialog) {
            WSDialog(
                title = stringResource(R.string.unblock_dialog_title),
                subTitle = "",
                okButtonText = stringResource(R.string.close),
                cancelButtonText = stringResource(R.string.unblock),
                okButtonClick = { showDialog = false },
                cancelButtonClick = {
                    action(EntireAction.UnBlockMessage)
                    showDialog = false
                },
                onDismissRequest = { },
            )
        }
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = false) { },
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    }

    LaunchedEffect(Unit) {
        action(EntireAction.OnBlockListScreenEntered)
    }
}
