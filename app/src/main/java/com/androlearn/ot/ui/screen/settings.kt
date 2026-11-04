package com.androlearn.ot.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.androlearn.ot.ui.components.ColorPickerSheet
import com.androlearn.ot.ui.theme.AppTheme
import com.androlearn.ot.ui.theme.faFont
import com.androlearn.ot.ui.theme.toHex

@Composable
fun Settings(
    navController: NavHostController,
    paddingValues: PaddingValues,
    isDarkTheme: Boolean,
    onThemeChange: () -> Unit,
    selectedTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit,
    customPrimary: Color,
    onCustomPrimaryChange: (Color) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = paddingValues.calculateTopPadding() + 20.dp,
                bottom = paddingValues.calculateBottomPadding() + 20.dp,
                start = 20.dp,
                end = 20.dp
            )
    ) {

        // عنوان
        Text(
            text = "تنظیمات",
            modifier = Modifier.fillMaxWidth(),
            fontFamily = faFont,
            fontSize = 26.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(28.dp))

        // ظاهر
        SettingsSectionTitle("ظاهر")

        Spacer(modifier = Modifier.height(10.dp))

        ThemeModeCard(
            isDarkTheme = isDarkTheme,
            onClick = onThemeChange
        )

        Spacer(modifier = Modifier.height(28.dp))

        // تم رنگی
        SettingsSectionTitle("تم رنگی")

        Spacer(modifier = Modifier.height(10.dp))

        ThemePresets(
            selectedTheme = selectedTheme,
            onThemeSelected = onThemeSelected
        )

        Spacer(modifier = Modifier.height(14.dp))

        CustomThemeCard(
            customPrimary = customPrimary,
            onCustomPrimaryChange = onCustomPrimaryChange
        )
    }
}

@Composable
private fun SettingsSectionTitle(
    title: String
) {
    Text(
        text = title,
        fontFamily = faFont,
        fontSize = 15.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun ThemeModeCard(
    isDarkTheme: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isDarkTheme) {
                    Icons.Default.DarkMode
                } else {
                    Icons.Default.LightMode
                },
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "حالت نمایش",
                fontFamily = faFont,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = if (isDarkTheme) {
                    "حالت تاریک"
                } else {
                    "حالت روشن"
                },
                fontFamily = faFont,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = onClick
        ) {
            Icon(
                imageVector = if (isDarkTheme) {
                    Icons.Default.LightMode
                } else {
                    Icons.Default.DarkMode
                },
                contentDescription = "تغییر حالت",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ThemePresets(
    selectedTheme: AppTheme,
    onThemeSelected: (AppTheme) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        ThemePresetCard(
            modifier = Modifier.weight(1f),
            name = "Emerald",
            primary = Color(0xFF00C896),
            secondary = Color(0xFF5EEAD4),
            selected = selectedTheme == AppTheme.EMERALD,
            onClick = {
                onThemeSelected(AppTheme.EMERALD)
            }
        )

        ThemePresetCard(
            modifier = Modifier.weight(1f),
            name = "Violet",
            primary = Color(0xFF8B5CF6),
            secondary = Color(0xFFC4B5FD),
            selected = selectedTheme == AppTheme.VIOLET,
            onClick = {
                onThemeSelected(AppTheme.VIOLET)
            }
        )

        ThemePresetCard(
            modifier = Modifier.weight(1f),
            name = "Ocean",
            primary = Color(0xFF3B82F6),
            secondary = Color(0xFF60A5FA),
            selected = selectedTheme == AppTheme.OCEAN,
            onClick = {
                onThemeSelected(AppTheme.OCEAN)
            }
        )
    }
}

@Composable
private fun ThemePresetCard(
    modifier: Modifier = Modifier,
    name: String,
    primary: Color,
    secondary: Color,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
                if (selected)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                else
                    MaterialTheme.colorScheme.surface
            )
            .clickable { onClick() }
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(primary)
            )

            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(secondary)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = name,
            fontFamily = faFont,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (selected) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "✓",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun CustomThemeCard(
    customPrimary: Color,
    onCustomPrimaryChange: (Color) -> Unit
) {

    var showColorPicker by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                showColorPicker = true
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(customPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Palette,
                contentDescription = null,
                tint = Color.White
            )
        }

        Spacer(
            modifier = Modifier.width(14.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {

            Text(
                text = "تم سفارشی",
                fontFamily = faFont,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(
                modifier = Modifier.height(3.dp)
            )

            Text(
                text = customPrimary.toHex(),
                fontFamily = faFont,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showColorPicker) {

        ColorPickerSheet(
            initialColor = customPrimary,

            onDismiss = {
                showColorPicker = false
            },

            onColorSelected = { color ->
                onCustomPrimaryChange(color)
            }
        )
    }
}