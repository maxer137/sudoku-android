package com.maxer137.sudoku.data

import android.content.Context
import androidx.core.content.edit
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

data class SavedGame(
    val difficulty: Difficulty,
    val seed: String,
    val sudoku: Sudoku,
    val elapsedMs: Long = 0,
) {
    companion object {
        fun new(difficulty: Difficulty, seed: String): SavedGame =
            SavedGame(difficulty, seed, Sudoku.puzzle(difficulty, Random(seed.toLong())))
    }
}

/**
 * The seed of the [index]th [difficulty] puzzle played on [date] (`yyyyMMdd`): the difficulty's
 * level, then the date, then the index from the second puzzle on.
 */
fun dailySeed(date: String, difficulty: Difficulty, index: Int): String =
    "${difficulty.level}$date" + if (index == 0) "" else "$index"

/** One token per cell, row by row: `.` empty, `g5` given, `f5` filled, `n135` notes. */
fun Sudoku.encode(): String =
    data.flatten().joinToString(",") { cell ->
        when (cell) {
            Cell.Empty -> "."
            is Cell.Given -> "g${cell.digit.value}"
            is Cell.Filled -> "f${cell.digit.value}"
            is Cell.Notes -> "n" + cell.digits.map { it.value }.sorted().joinToString("")
        }
    }

/** Inverse of [encode]. Throws if [text] isn't a valid encoding. */
fun Sudoku.Companion.decode(text: String): Sudoku {
    val cells = text.split(",").map { token ->
        val digits = token.drop(1).map { Digit(it.digitToInt()) }
        when (token.first()) {
            '.' -> Cell.Empty
            'g' -> Cell.Given(digits.single())
            'f' -> Cell.Filled(digits.single())
            'n' -> Cell.Notes(digits.toSet())
            else -> throw IllegalArgumentException("Unknown cell token '$token'")
        }
    }
    return Sudoku(cells.chunked(SIZE))
}


class GameStore(context: Context) {
    private val prefs = context.getSharedPreferences("game", Context.MODE_PRIVATE)

    fun load(): SavedGame? = runCatching {
        SavedGame(
            difficulty = Difficulty.valueOf(prefs.getString(KEY_DIFFICULTY, null) ?: return null),
            seed = prefs.getString(KEY_SEED, null) ?: return null,
            sudoku = Sudoku.decode(prefs.getString(KEY_GRID, null) ?: return null),
            elapsedMs = prefs.getLong(KEY_ELAPSED, 0),
        )
    }.getOrNull()

    fun save(game: SavedGame) = prefs.edit {
        putString(KEY_DIFFICULTY, game.difficulty.name)
        putString(KEY_SEED, game.seed)
        putString(KEY_GRID, game.sudoku.encode())
        putLong(KEY_ELAPSED, game.elapsedMs)
    }

    fun nextSeed(difficulty: Difficulty, today: String = today()): String {
        val isNewDay = prefs.getString(KEY_DATE, null) != today
        val index = if (isNewDay) 0 else prefs.getInt(countKey(difficulty), 0)
        prefs.edit {
            if (isNewDay) {
                putString(KEY_DATE, today)
                Difficulty.entries.forEach { remove(countKey(it)) }
            }
            putInt(countKey(difficulty), index + 1)
        }
        return dailySeed(today, difficulty, index)
    }

    private companion object {
        const val KEY_DIFFICULTY = "difficulty"
        const val KEY_SEED = "seed"
        const val KEY_GRID = "grid"
        const val KEY_ELAPSED = "elapsed_ms"
        const val KEY_DATE = "date"

        fun countKey(difficulty: Difficulty) = "count_${difficulty.name}"

        fun today(): String = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
    }
}
