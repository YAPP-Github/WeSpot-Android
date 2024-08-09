package com.bff.wespot.designsystem.component.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bff.wespot.designsystem.R
import com.bff.wespot.designsystem.theme.Gray400
import com.bff.wespot.designsystem.theme.Primary400
import com.bff.wespot.designsystem.theme.StaticTypeScale
import com.bff.wespot.designsystem.theme.WeSpotTheme
import com.bff.wespot.designsystem.theme.WeSpotThemeManager
import com.bff.wespot.designsystem.util.OrientationPreviews

@Composable
fun WsTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean = false,
    singleLine: Boolean = false,
    readOnly: Boolean = false,
    focusRequester: FocusRequester = remember { FocusRequester() },
    onFocusChanged: (FocusState) -> Unit = { },
    keyBoardOption: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    textFieldType: WsTextFieldType = WsTextFieldType.Normal,
) {
    var textFieldValueState by remember { mutableStateOf(TextFieldValue(text = value)) }

    LaunchedEffect(value) {
        if (value != textFieldValueState.text) {
            textFieldValueState = textFieldValueState.copy(text = value)
        }
    }

    Box(modifier = Modifier.wrapContentSize()) {
        OutlinedTextField(
            modifier = Modifier
                .heightIn(
                    min = textFieldType.minHeight(),
                    max = textFieldType.maxHeight(),
                )
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusChanged { focusState -> onFocusChanged(focusState) },
            value = textFieldValueState,
            onValueChange = { newTextFieldValue ->
                textFieldValueState = newTextFieldValue
                onValueChange(newTextFieldValue.text)
            },
            isError = isError,
            singleLine = singleLine,
            readOnly = readOnly,
            enabled = textFieldType.isEnabled(),
            keyboardOptions = keyBoardOption,
            keyboardActions = keyboardActions,
            trailingIcon = if (textFieldType.trailingIcon() != null) {
                {
                    Icon(
                        painter = textFieldType.trailingIcon()!!,
                        contentDescription = stringResource(id = R.string.textfield_trailing_icon),
                    )
                }
            } else {
                null
            },
            leadingIcon = if (textFieldType.leadingIcon() != null) {
                {
                    Icon(
                        painter = textFieldType.leadingIcon()!!,
                        contentDescription = stringResource(id = R.string.textfield_leading_icon),
                    )
                }
            } else {
                null
            },
            placeholder = {
                Text(
                    text = placeholder,
                    style = StaticTypeScale.Default.body4,
                    color = Gray400,
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = WeSpotThemeManager.colors.cardBackgroundColor,
                unfocusedContainerColor = WeSpotThemeManager.colors.cardBackgroundColor,
                focusedPlaceholderColor = WeSpotThemeManager.colors.disableBtnTxtColor,
                unfocusedPlaceholderColor = WeSpotThemeManager.colors.disableBtnTxtColor,
                focusedBorderColor = if (textFieldType == WsTextFieldType.Message) {
                    WeSpotThemeManager.colors.cardBackgroundColor
                } else {
                    Primary400
                },
                unfocusedBorderColor = WeSpotThemeManager.colors.cardBackgroundColor,
                disabledContainerColor = WeSpotThemeManager.colors.cardBackgroundColor,
                disabledPlaceholderColor = Gray400,
                disabledBorderColor = WeSpotThemeManager.colors.cardBackgroundColor,
                errorBorderColor = WeSpotThemeManager.colors.dangerColor,
                errorContainerColor = WeSpotThemeManager.colors.cardBackgroundColor,
                errorPlaceholderColor = WeSpotThemeManager.colors.disableBtnTxtColor,
            ),
            shape = WeSpotThemeManager.shapes.small,
            textStyle = StaticTypeScale.Default.body4,
        )
    }
}

sealed interface WsTextFieldType {
    @Composable
    fun trailingIcon(): Painter?

    @Composable
    fun leadingIcon(): Painter?

    fun minHeight(): Dp = Dp.Unspecified

    fun maxHeight(): Dp = Dp.Unspecified

    fun isEnabled(): Boolean = true

    data object Normal : WsTextFieldType {
        @Composable
        override fun trailingIcon() = null

        @Composable
        override fun leadingIcon() = null
    }

    data object Search : WsTextFieldType {
        @Composable
        override fun trailingIcon() = null

        @Composable
        override fun leadingIcon() = painterResource(id = R.drawable.search)
    }

    data object Lock : WsTextFieldType {
        @Composable
        override fun trailingIcon() = painterResource(id = R.drawable.lock)

        @Composable
        override fun leadingIcon() = null

        override fun isEnabled(): Boolean = false
    }

    data object Message : WsTextFieldType {
        @Composable
        override fun trailingIcon() = null

        @Composable
        override fun leadingIcon() = null

        override fun minHeight() = 170.dp

        override fun maxHeight() = 228.dp
    }
}

@OrientationPreviews
@Composable
private fun WsTextFieldPreview() {
    val (value, onValueChanged) = remember { mutableStateOf("") }

    WeSpotTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                WsTextField(
                    value = value,
                    onValueChange = onValueChanged,
                    placeholder = stringResource(id = R.string.search),
                    textFieldType = WsTextFieldType.Normal,
                )

                WsTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = stringResource(id = R.string.search),
                    textFieldType = WsTextFieldType.Search,
                )

                WsTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = stringResource(id = R.string.search),
                    textFieldType = WsTextFieldType.Lock,
                )

                WsTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = stringResource(id = R.string.search),
                    isError = true,
                    textFieldType = WsTextFieldType.Normal,
                )
            }
        }
    }
}

@OrientationPreviews
@Composable
private fun MessagePreview() {
    val (value, onValueChanged) = remember { mutableStateOf("") }

    WeSpotTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                WsTextField(
                    value = value,
                    onValueChange = onValueChanged,
                    placeholder = "Message",
                    textFieldType = WsTextFieldType.Message,
                )
            }
        }
    }
}
