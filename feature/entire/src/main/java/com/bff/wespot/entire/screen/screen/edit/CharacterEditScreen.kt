package com.bff.wespot.entire.screen.screen.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bff.wespot.designsystem.component.header.WSTopBar
import com.bff.wespot.entire.screen.state.edit.EntireEditAction
import com.bff.wespot.entire.screen.state.edit.EntireEditSideEffect
import com.bff.wespot.entire.screen.viewmodel.EntireEditViewModel
import com.bff.wespot.model.user.response.ProfileCharacter
import com.bff.wespot.ui.CharacterScreen
import com.ramcosta.composedestinations.annotation.Destination
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

interface CharacterEditNavigator {
    fun navigateUp()
    fun navigateToProfileEditScreen(args: ProfileEditNavArgs)
}

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun CharacterEditScreen(
    navigator: CharacterEditNavigator,
    viewModel: EntireEditViewModel = hiltViewModel(),
) {
    val color by viewModel.backgroundColor.collectAsStateWithLifecycle()
    val character by viewModel.characters.collectAsStateWithLifecycle()

    val action = viewModel::onAction
    val state by viewModel.collectAsState()
    viewModel.collectSideEffect {
        when (it) {
            EntireEditSideEffect.NavigateToEntire -> {
                navigator.navigateToProfileEditScreen(
                    ProfileEditNavArgs(true),
                )
            }
            else -> { }
        }
    }

    if (color.isEmpty() || character.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            WSTopBar(
                title = "",
                canNavigateBack = true,
                navigateUp = { navigator.navigateUp() },
            )
        },
    ) {
        Box(modifier = Modifier.padding(it)) {
            CharacterScreen(
                name = state.profile.name,
                isEditing = true,
                characterList = character,
                colorList = color,
            ) { iconUrl, color ->
                action(
                    EntireEditAction.OnCharacterEditDoneButtonClicked(
                        ProfileCharacter(iconUrl, color),
                    ),
                )
            }
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
}
