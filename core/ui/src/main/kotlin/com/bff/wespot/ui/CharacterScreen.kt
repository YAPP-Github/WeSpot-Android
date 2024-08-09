package com.bff.wespot.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bff.wespot.designsystem.component.button.WSButton
import com.bff.wespot.designsystem.theme.StaticTypeScale
import com.bff.wespot.designsystem.theme.WeSpotThemeManager
import com.bff.wespot.model.common.BackgroundColor
import com.bff.wespot.model.common.Character
import com.bff.wespot.util.hexToColor

@Composable
fun CharacterScreen(
    name: String,
    characterList: List<Character>,
    colorList: List<BackgroundColor>,
    isEditing: Boolean = false,
    navigateToNext: (String, String) -> Unit,
) {
    require(characterList.isNotEmpty()) { "Character list should not be empty" }
    require(colorList.isNotEmpty()) { "Color list should not be empty" }

    var character by remember {
        mutableStateOf(characterList.first().iconUrl)
    }

    var color by remember {
        mutableStateOf(colorList.first().color)
    }

    var chipSelection by remember {
        mutableIntStateOf(CHARACTER)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        Text(
            text = stringResource(R.string.select_character, name),
            style = StaticTypeScale.Default.header1,
            modifier = Modifier.padding(horizontal = 24.dp),
        )

        Spacer(modifier = Modifier.height(48.dp))

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(hexToColor(color)),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    model = ImageRequest
                        .Builder(LocalContext.current)
                        .data(character)
                        .build(),
                    contentDescription = stringResource(id = R.string.user_character_image),
                    modifier = Modifier.size(75.dp),
                )
            }
        }

        Spacer(modifier = Modifier.height(42.dp))

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
            Column {
                SelectionTypeChipGroup(chipSelection) {
                    chipSelection = it
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WeSpotThemeManager.colors.modalColor),
                ) {
                    when (chipSelection) {
                        CHARACTER -> {
                            CharacterPickerBox(
                                selected = character,
                                characterList = characterList,
                                onClick = {
                                    character = it
                                },
                            )
                        }

                        COLOR -> {
                            ColorPickerBox(
                                selected = color,
                                colorList = colorList,
                                onClick = {
                                    color = it
                                },
                            )
                        }
                    }

                    WSButton(
                        onClick = {
                            navigateToNext(character, color)
                        },
                        text = stringResource(
                            if (isEditing) R.string.edit_done else R.string.complete,
                        ),
                    ) {
                        it.invoke()
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectionTypeChipGroup(index: Int, onClick: (Int) -> Unit) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    val chipWidth = remember {
        (screenWidth - 64.dp) / 2
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SelectionTypeChip(
            text = stringResource(R.string.character),
            isSelected = index == CHARACTER,
            onClick = {
                onClick(CHARACTER)
            },
            icon = painterResource(id = R.drawable.character),
            modifier = Modifier.width(chipWidth),
        )

        SelectionTypeChip(
            text = stringResource(R.string.background),
            isSelected = index == COLOR,
            onClick = {
                onClick(COLOR)
            },
            icon = painterResource(id = R.drawable.background),
            modifier = Modifier.width(chipWidth),
        )
    }
}

@Composable
private fun SelectionTypeChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: Painter,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        shape = WeSpotThemeManager.shapes.extraLarge,
        selected = isSelected,
        onClick = onClick,
        label = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Image(
                    painter = icon,
                    contentDescription = text,
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(
                        if (isSelected) {
                            Color(0xFFD9D9D9)
                        } else {
                            Color(0xFF76777D)
                        },
                    ),
                )
                Text(text = text, style = StaticTypeScale.Default.body6)
            }
        },
        border = if (!isSelected) {
            BorderStroke(
                width = 1.dp,
                color = WeSpotThemeManager.colors.disableIcnColor,
            )
        } else {
            null
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = WeSpotThemeManager.colors.backgroundColor,
            labelColor = WeSpotThemeManager.colors.disableIcnColor,
            selectedContainerColor = WeSpotThemeManager.colors.secondaryBtnColor,
            selectedLabelColor = Color(0xFFF7F7F8),
        ),
        modifier = modifier,
    )
}

@Composable
private fun CharacterPickerBox(
    selected: String,
    characterList: List<Character>,
    onClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(id = R.string.character),
            style = StaticTypeScale.Default.body6,
            color = Color(0xFFAEAFB4),
        )

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth(),
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
        ) {
            items(characterList, key = {
                it.id
            }) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .clickable { onClick(it.iconUrl) }
                        .let { modifier ->
                            if (it.iconUrl == selected) {
                                modifier
                                    .background(WeSpotThemeManager.colors.badgeColor)
                                    .border(2.dp, Color.White, CircleShape)
                            } else {
                                modifier
                            }
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    AsyncImage(
                        model = ImageRequest
                            .Builder(LocalContext.current)
                            .data(it.iconUrl)
                            .build(),
                        contentDescription = stringResource(id = R.string.user_character_image),
                        modifier = Modifier
                            .size(60.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ColorPickerBox(
    selected: String,
    colorList: List<BackgroundColor>,
    onClick: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = stringResource(id = R.string.background),
            style = StaticTypeScale.Default.body6,
        )

        LazyVerticalGrid(
            modifier = Modifier
                .fillMaxWidth(),
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
        ) {
            items(colorList, key = {
                it.id
            }) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(hexToColor(it.color))
                        .zIndex(1f)
                        .clickable { onClick(it.color) }
                        .let { modifier ->
                            if (it.color == selected) {
                                modifier.border(2.dp, Color.White, CircleShape)
                            } else {
                                modifier
                            }
                        },
                )
            }
        }
    }
}

private const val CHARACTER = 0
private const val COLOR = 1
