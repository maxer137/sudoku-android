package com.maxer137.sudoku.ui.theme

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers

/**
 * Renders a preview in light and dark mode against several wallpapers
 * Since dynamic color (Android 12+) derives the whole color scheme from the device's wallpaper.
 */
@Preview(name = "Light · default", group = "light", uiMode = UI_MODE_NIGHT_NO or UI_MODE_TYPE_NORMAL)
@Preview(name = "Light · red", group = "light", uiMode = UI_MODE_NIGHT_NO or UI_MODE_TYPE_NORMAL, wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE)
@Preview(name = "Light · green", group = "light", uiMode = UI_MODE_NIGHT_NO or UI_MODE_TYPE_NORMAL, wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE)
@Preview(name = "Light · blue", group = "light", uiMode = UI_MODE_NIGHT_NO or UI_MODE_TYPE_NORMAL, wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE)
@Preview(name = "Light · yellow", group = "light", uiMode = UI_MODE_NIGHT_NO or UI_MODE_TYPE_NORMAL, wallpaper = Wallpapers.YELLOW_DOMINATED_EXAMPLE)
@Preview(name = "Dark · default", group = "dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL)
@Preview(name = "Dark · red", group = "dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL, wallpaper = Wallpapers.RED_DOMINATED_EXAMPLE)
@Preview(name = "Dark · green", group = "dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL, wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE)
@Preview(name = "Dark · blue", group = "dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL, wallpaper = Wallpapers.BLUE_DOMINATED_EXAMPLE)
@Preview(name = "Dark · yellow", group = "dark", uiMode = UI_MODE_NIGHT_YES or UI_MODE_TYPE_NORMAL, wallpaper = Wallpapers.YELLOW_DOMINATED_EXAMPLE)
annotation class ThemePreviews
