package com.bff.wespot.entire.screen.screen.edit

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bff.wespot.designsystem.component.button.WSButton
import com.bff.wespot.designsystem.component.header.WSTopBar
import com.bff.wespot.designsystem.component.indicator.WSToast
import com.bff.wespot.designsystem.component.indicator.WSToastType
import com.bff.wespot.designsystem.component.input.WsTextField
import com.bff.wespot.designsystem.component.input.WsTextFieldType
import com.bff.wespot.designsystem.theme.StaticTypeScale
import com.bff.wespot.designsystem.theme.WeSpotThemeManager
import com.bff.wespot.entire.R
import com.bff.wespot.entire.screen.common.INTRODUCTION_MAX_LENGTH
import com.bff.wespot.entire.screen.state.edit.EntireEditAction
import com.bff.wespot.entire.screen.state.edit.EntireEditSideEffect
import com.bff.wespot.entire.screen.viewmodel.EntireEditViewModel
import com.bff.wespot.model.ToastState
import com.bff.wespot.ui.LetterCountIndicator
import com.bff.wespot.util.hexToColor
import com.ramcosta.composedestinations.annotation.Destination
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

interface ProfileEditNavigator {
    fun navigateUp()
    fun navigateToCharacterEditScreen()
}

data class ProfileEditNavArgs(
    val isCompleteProfileEdit: Boolean,
)

@OptIn(ExperimentalMaterial3Api::class)
@Destination(navArgsDelegate = ProfileEditNavArgs::class)
@Composable
fun ProfileEditScreen(
    navigator: ProfileEditNavigator,
    navArgs: ProfileEditNavArgs,
    viewModel: EntireEditViewModel = hiltViewModel(),
) {
    var toast by remember { mutableStateOf(ToastState()) }
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    val action = viewModel::onAction
    val state by viewModel.collectAsState()
    viewModel.collectSideEffect {
        when (it) {
            is EntireEditSideEffect.ShowToast -> {
                toast = it.toastState
                focusManager.clearFocus()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            WSTopBar(
                title = stringResource(id = R.string.profile_edit),
                canNavigateBack = true,
                navigateUp = { navigator.navigateUp() },
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(modifier = Modifier.clickable { navigator.navigateToCharacterEditScreen() }) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .padding(top = 16.dp)
                        .background(hexToColor(state.profile.profileCharacter.backgroundColor)),
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        modifier = Modifier.size(90.dp),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(state.profile.profileCharacter.iconUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = stringResource(com.bff.wespot.ui.R.string.user_character_image),
                    )
                }

                Image(
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.BottomEnd)
                        .zIndex(1f),
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = stringResource(R.string.edit_icon),
                )
            }

            ProfileEditLockedItem(
                title = stringResource(R.string.name),
                content = state.profile.name,
                onClick = {
                    focusManager.clearFocus()
                    toast = ToastState(
                        show = true,
                        message = R.string.request_profile_edit_text,
                        type = WSToastType.Error,
                    )
                },
            )

            ProfileEditLockedItem(
                title = stringResource(R.string.gender),
                content = state.profile.gender,
                onClick = {
                    focusManager.clearFocus()
                    toast = ToastState(
                        show = true,
                        message = R.string.request_profile_edit_text,
                        type = WSToastType.Error,
                    )
                },
            )

            ProfileEditLockedItem(
                title = stringResource(R.string.school_info),
                content = state.profile.toSchoolInfo(),
                onClick = {
                    focusManager.clearFocus()
                    toast = ToastState(
                        show = true,
                        message = R.string.request_profile_edit_text,
                        type = WSToastType.Error,
                    )
                },
            )

            ProfileIntroductionItem(
                title = stringResource(R.string.introduction),
                content = state.introductionInput,
                hasProfanity = state.hasProfanity,
                onValueChange = { value -> action(EntireEditAction.OnIntroductionChanged(value)) },
                onFocusChanged = { focusState ->
                    if (focusState.isFocused) {
                        // 포커스 상태일 떄, 화면 제일 하단으로 스크롤
                        coroutineScope.launch {
                            delay(500)
                            scrollState.animateScrollTo(scrollState.maxValue)
                        }
                    }
                    action(EntireEditAction.OnProfileEditTextFieldFocused(focusState.isFocused))
                },
            )

            Spacer(modifier = Modifier.height(72.dp))
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            if (state.isIntroductionEditing) {
                val isEdited = state.profile.introduction != state.introductionInput
                WSButton(
                    onClick = {
                        action(EntireEditAction.OnIntroductionEditDoneButtonClicked)
                    },
                    enabled =
                        isEdited &&
                            state.hasProfanity.not() &&
                            state.introductionInput.length in 1..20,
                    text = stringResource(id = R.string.edit_done),
                    content = { it() },
                )
            } else {
                WSButton(
                    // TODO 카카오톡 리다이렉션
                    onClick = { },
                    text = stringResource(R.string.request_change_profile),
                    content = { it() },
                )
            }
        }
    }

    if (toast.show) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            WSToast(
                text = stringResource(toast.message),
                showToast = toast.show,
                toastType = toast.type,
                closeToast = {
                    toast = toast.copy(show = false)
                },
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
        action(EntireEditAction.OnProfileEditScreenEntered(navArgs.isCompleteProfileEdit))
    }
}

@Composable
fun ProfileEditLockedItem(
    title: String,
    content: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            modifier = Modifier.padding(start = 10.dp),
            text = title,
            style = StaticTypeScale.Default.body4,
            color = WeSpotThemeManager.colors.txtTitleColor,
        )

        Box(modifier = Modifier.clickable { onClick() }) {
            WsTextField(
                value = "",
                onValueChange = {},
                placeholder = content,
                textFieldType = WsTextFieldType.Lock,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun ProfileIntroductionItem(
    title: String,
    content: String,
    hasProfanity: Boolean,
    onValueChange: (String) -> Unit,
    onFocusChanged: (FocusState) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(start = 10.dp),
            text = title,
            style = StaticTypeScale.Default.body4,
            color = WeSpotThemeManager.colors.txtTitleColor,
        )

        Spacer(modifier = Modifier.height(12.dp))

        WsTextField(
            value = content,
            onValueChange = onValueChange,
            placeholder = "",
            onFocusChanged = onFocusChanged,
            textFieldType = WsTextFieldType.Normal,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            val warningMessage = when {
                content.length > INTRODUCTION_MAX_LENGTH -> {
                    stringResource(R.string.introduction_limit)
                }
                hasProfanity -> {
                    stringResource(com.bff.wespot.designsystem.R.string.has_profanity)
                }
                else -> ""
            }

            Text(
                modifier = Modifier.padding(top = 5.dp, start = 10.dp, end = 10.dp),
                text = warningMessage,
                style = StaticTypeScale.Default.body7,
                color = WeSpotThemeManager.colors.dangerColor,
            )

            LetterCountIndicator(currentCount = content.length, maxCount = 20)
        }
    }
}
