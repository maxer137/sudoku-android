package com.maxer137.sudoku.ui

import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.maxer137.sudoku.R
import com.maxer137.sudoku.data.Cell
import com.maxer137.sudoku.data.Difficulty
import com.maxer137.sudoku.data.Digit
import com.maxer137.sudoku.data.GameTimer
import com.maxer137.sudoku.data.Pos
import com.maxer137.sudoku.data.SavedGame
import com.maxer137.sudoku.data.Settings
import com.maxer137.sudoku.data.Sudoku
import com.maxer137.sudoku.data.digitOrNull
import com.maxer137.sudoku.ui.theme.SudokuTheme
import kotlinx.coroutines.delay

@Composable
fun SudokuScreen(
    game: SavedGame,
    modifier: Modifier = Modifier,
    onGameChange: (SavedGame) -> Unit = {},
    onExit: () -> Unit = {},
    settings: Settings = Settings(),
    onSettingsChange: (Settings) -> Unit = {},
) {
    var sudoku by remember { mutableStateOf(game.sudoku) }
    var history by remember { mutableStateOf(emptyList<Sudoku>()) }
    var selected by remember { mutableStateOf<Pos?>(null) }
    var notesMode by remember { mutableStateOf(false) }
    var timer by remember { mutableStateOf(GameTimer(accumulatedMs = game.elapsedMs)) }
    var isResumed by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var fillResult by remember { mutableStateOf<FillResult?>(null) }

    fun updateTimer() {
        val now = SystemClock.elapsedRealtime()
        val isPlaying = isResumed && !showSettings && !sudoku.isSolved
        timer = if (isPlaying) timer.start(now) else timer.pause(now)
        onGameChange(game.copy(sudoku = sudoku, elapsedMs = timer.elapsedMs(now)))
    }

    fun updateSudoku(new: Sudoku) {
        if (new == sudoku) return
        history = history + sudoku
        sudoku = new
        if (new.isFull) fillResult = if (new.isSolved) FillResult.Solved else FillResult.Mistakes
        updateTimer()
    }

    val canUndo = history.isNotEmpty() && !sudoku.isSolved

    fun undo() {
        if (!canUndo) return
        sudoku = history.last()
        history = history.dropLast(1)
        updateTimer()
    }

    fun setShowSettings(show: Boolean) {
        showSettings = show
        updateTimer()
    }

    LifecycleResumeEffect(Unit) {
        isResumed = true
        updateTimer()
        onPauseOrDispose {
            isResumed = false
            updateTimer()
        }
    }

    fun setSelected(cell: Cell) {
        selected?.let { updateSudoku(sudoku.with(it.row, it.col, cell)) }
    }

    val header: @Composable () -> Unit = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (settings.showTimer) TimerText(timer)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { undo() }, enabled = canUndo) {
                Icon(painterResource(R.drawable.ic_undo), contentDescription = "Undo")
            }
            IconButton(onClick = { setShowSettings(true) }) {
                Icon(painterResource(R.drawable.ic_settings), contentDescription = "Settings")
            }
        }
    }
    val grid: @Composable (Modifier) -> Unit = { gridModifier ->
        SudokuGrid(
            sudoku = sudoku,
            selected = selected,
            onCellClick = { selected = it },
            modifier = gridModifier,
            highlightRowAndColumn = settings.highlightRowAndColumn,
            highlightSameDigits = settings.highlightSameDigits,
            showConflicts = settings.showConflicts,
        )
    }
    val numberPad: @Composable (columns: Int, Modifier) -> Unit = { columns, padModifier ->
        NumberPad(
            columns = columns,
            notesMode = notesMode,
            onDigit = { d ->
                if (notesMode) {
                    selected?.let { updateSudoku(sudoku.toggleNote(it.row, it.col, Digit(d))) }
                } else {
                    setSelected(Cell.Filled(Digit(d)))
                }
            },
            onClear = { setSelected(Cell.Empty) },
            onToggleNotes = { notesMode = !notesMode },
            modifier = padModifier,
        )
    }

    BoxWithConstraints(modifier.padding(ScreenPadding)) {
        if (maxWidth > maxHeight) {
            // grid and pad each get their own half,
            // so on a foldable neither crosses the crease
            val halfWidth = (maxWidth - Gap) / 2
            val gridSize = minOf(halfWidth, maxHeight)
            val padAlignment = if (settings.numberPadOnLeft) Alignment.BottomStart else Alignment.BottomEnd
            Row(horizontalArrangement = Arrangement.spacedBy(Gap)) {
                val gridHalf = @Composable {
                    Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                        grid(Modifier.size(gridSize))
                    }
                }
                if (!settings.numberPadOnLeft) gridHalf()
                Column(Modifier.weight(1f).fillMaxHeight()) {
                    header()
                    Box(Modifier.weight(1f).fillMaxWidth(), contentAlignment = padAlignment) {
                        numberPad(3, Modifier.width(minOf(KeypadWidth, halfWidth)))
                    }
                }
                if (settings.numberPadOnLeft) gridHalf()
            }
        } else {
            // shrink the grid when the screen is too short to fit everything
            val gridSize = minOf(maxWidth, maxHeight - HeaderHeight - NumberPadHeight - Gap * 2)
            Column(
                verticalArrangement = Arrangement.spacedBy(Gap),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                header()
                grid(Modifier.size(gridSize.coerceAtLeast(0.dp)))
                numberPad(5, Modifier)
            }
        }
    }

    if (showSettings) {
        SettingsSheet(
            settings = settings,
            onSettingsChange = onSettingsChange,
            onDismiss = { setShowSettings(false) },
        )
    }

    fillResult?.let { result ->
        FillResultDialog(
            result = result,
            elapsedMs = timer.elapsedMs(SystemClock.elapsedRealtime()),
            onDismiss = { fillResult = null },
            onExit = onExit,
        )
    }
}

private val ScreenPadding = 16.dp
private val Gap = 16.dp
private val KeyHeight = 48.dp
// rough sizes of the controls, only used to fit the grid around them
private val HeaderHeight = 48.dp
private val NumberPadHeight = KeyHeight * 3 + 8.dp * 2
private val KeypadWidth = 240.dp

private enum class FillResult { Solved, Mistakes }

@Composable
private fun FillResultDialog(
    result: FillResult,
    elapsedMs: Long,
    onDismiss: () -> Unit,
    onExit: () -> Unit,
) {
    when (result) {
        FillResult.Solved -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Congratulations!") },
            text = { Text("You solved the puzzle in ${formatElapsed(elapsedMs)}.") },
            confirmButton = { TextButton(onClick = onExit) { Text("Main menu") } },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Close") } },
        )
        FillResult.Mistakes -> AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Not quite") },
            text = { Text("The grid is full, but there are mistakes. Keep looking!") },
            confirmButton = { TextButton(onClick = onDismiss) { Text("OK") } },
        )
    }
}

@Composable
fun SudokuGrid(
    sudoku: Sudoku,
    selected: Pos?,
    onCellClick: (Pos) -> Unit,
    modifier: Modifier = Modifier,
    highlightRowAndColumn: Boolean = true,
    highlightSameDigits: Boolean = true,
    showConflicts: Boolean = true,
) {
    val lineColor = MaterialTheme.colorScheme.onSurface
    val selectedDigit = selected?.let { sudoku[it.row, it.col].digitOrNull }
    val conflicts = remember(sudoku, showConflicts) {
        if (showConflicts) sudoku.conflicts() else emptySet()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .border(2.dp, lineColor),
    ) {
        for (boxRow in 0 until 3) {
            Row(Modifier.weight(1f)) {
                for (boxCol in 0 until 3) {
                    Column(
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .border(1.dp, lineColor)
                    ) {
                        for (row in boxRow * 3 until boxRow * 3 + 3) {
                            Row(Modifier.weight(1f)) {
                                for (col in boxCol * 3 until boxCol * 3 + 3) {
                                    val pos = Pos(row, col)
                                    val cell = sudoku[row, col]
                                    SudokuCell(
                                        cell = cell,
                                        highlight = when {
                                            pos == selected -> Highlight.Selected
                                            highlightSameDigits && selectedDigit != null &&
                                                cell.digitOrNull == selectedDigit -> Highlight.SameDigit
                                            highlightRowAndColumn && selected != null &&
                                                (row == selected.row || col == selected.col) -> Highlight.Peer
                                            else -> Highlight.None
                                        },
                                        noteHighlight = selectedDigit.takeIf { highlightSameDigits },
                                        isConflict = pos in conflicts,
                                        onClick = { onCellClick(pos) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .fillMaxHeight(),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private enum class Highlight { Selected, SameDigit, Peer, None }

@Composable
private fun SudokuCell(
    cell: Cell,
    highlight: Highlight,
    noteHighlight: Digit?,
    isConflict: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .border(0.5.dp, colors.outline)
            .background(if (cell is Cell.Given) colors.secondaryContainer else Color.Transparent)
            .background(
                when (highlight) {
                    Highlight.Selected -> colors.primaryContainer
                    Highlight.SameDigit -> colors.primary.copy(alpha = 0.3f)
                    Highlight.Peer -> colors.primary.copy(alpha = 0.1f)
                    Highlight.None -> Color.Transparent
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (cell is Cell.Notes) {
            NotesGrid(cell.digits, highlighted = noteHighlight)
        } else {
            Text(
                text = cell.digitOrNull?.value?.toString() ?: "",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = if (cell is Cell.Given) FontWeight.Bold else FontWeight.Normal,
            )
        }
        if (isConflict) {
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(3.dp)
                    .size(6.dp)
                    .background(colors.error, CircleShape)
            )
        }
    }
}

@Composable
private fun NotesGrid(digits: Set<Digit>, highlighted: Digit?, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(1.dp)) {
        for (r in 0 until 3) {
            Row(Modifier.weight(1f)) {
                for (c in 0 until 3) {
                    val digit = Digit(r * 3 + c + 1)
                    Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                        if (digit in digits) {
                            val isHighlighted = digit == highlighted
                            Text(
                                text = digit.value.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
                                color = if (isHighlighted) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Shows [timer]'s elapsed time. Rather than polling, it sleeps until the displayed
 * second actually changes, and stops waking up entirely while the timer is paused.
 */
@Composable
private fun TimerText(timer: GameTimer, modifier: Modifier = Modifier) {
    var elapsedMs by remember { mutableLongStateOf(timer.elapsedMs(SystemClock.elapsedRealtime())) }
    LaunchedEffect(timer) {
        elapsedMs = timer.elapsedMs(SystemClock.elapsedRealtime())
        while (timer.isRunning) {
            delay(1000 - elapsedMs % 1000)
            elapsedMs = timer.elapsedMs(SystemClock.elapsedRealtime())
        }
    }
    Text(
        text = formatElapsed(elapsedMs),
        style = MaterialTheme.typography.titleMedium.copy(fontFeatureSettings = "tnum"),
        modifier = modifier,
    )
}

/** `m:ss`, or `h:mm:ss` once past the hour. */
private fun formatElapsed(ms: Long): String {
    val total = ms / 1000
    val (h, m, s) = Triple(total / 3600, total / 60 % 60, total % 60)
    return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
}

// less than the default, so "Notes: off" still fits on one line in the keypad
private val WideKeyPadding = PaddingValues(horizontal = 8.dp)

@Composable
private fun NumberPad(
    columns: Int,
    notesMode: Boolean,
    onDigit: (Int) -> Unit,
    onClear: () -> Unit,
    onToggleNotes: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        (1..9).chunked(columns).forEach { digits ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                digits.forEach { d ->
                    Button(onClick = { onDigit(d) }, modifier = Modifier.weight(1f).height(KeyHeight)) {
                        Text(d.toString())
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onClear, modifier = Modifier.weight(1f).height(KeyHeight), contentPadding = WideKeyPadding) {
                Text("Clear")
            }
            if (notesMode) {
                FilledTonalButton(onClick = onToggleNotes, modifier = Modifier.weight(1f).height(KeyHeight), contentPadding = WideKeyPadding) {
                    Text("Notes: on")
                }
            } else {
                OutlinedButton(onClick = onToggleNotes, modifier = Modifier.weight(1f).height(KeyHeight), contentPadding = WideKeyPadding) {
                    Text("Notes: off")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SudokuScreenPreview() {
    SudokuTheme { SudokuScreen(game = SavedGame.new(Difficulty.MEDIUM, "20260928")) }
}
