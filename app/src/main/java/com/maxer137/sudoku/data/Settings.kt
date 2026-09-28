package com.maxer137.sudoku.data

import android.content.Context
import androidx.core.content.edit

/** User preferences for how the game looks and behaves. */
data class Settings(
    /** Tint the row and column of the selected cell. */
    val highlightRowAndColumn: Boolean = true,
    /** Tint every cell holding the same digit as the selected cell. */
    val highlightSameDigits: Boolean = true,
    /** Show how long the player has been actively working on the puzzle. */
    val showTimer: Boolean = true,
)

/** Loads and saves [Settings] across app restarts. */
class SettingsStore(context: Context) {
    private val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    fun load(): Settings {
        val defaults = Settings()
        return Settings(
            highlightRowAndColumn = prefs.getBoolean(KEY_ROW_COL, defaults.highlightRowAndColumn),
            highlightSameDigits = prefs.getBoolean(KEY_SAME_DIGITS, defaults.highlightSameDigits),
            showTimer = prefs.getBoolean(KEY_TIMER, defaults.showTimer),
        )
    }

    fun save(settings: Settings) = prefs.edit {
        putBoolean(KEY_ROW_COL, settings.highlightRowAndColumn)
        putBoolean(KEY_SAME_DIGITS, settings.highlightSameDigits)
        putBoolean(KEY_TIMER, settings.showTimer)
    }

    private companion object {
        const val KEY_ROW_COL = "highlight_row_and_column"
        const val KEY_SAME_DIGITS = "highlight_same_digits"
        const val KEY_TIMER = "show_timer"
    }
}
