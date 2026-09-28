package com.maxer137.sudoku.ui

import android.os.SystemClock
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import com.maxer137.sudoku.data.Settings
import com.maxer137.sudoku.data.Sudoku
import com.maxer137.sudoku.data.digitOrNull
import com.maxer137.sudoku.ui.theme.SudokuTheme
import kotlinx.coroutines.delay

@Composable
fun SudokuScreen(
    modifier: Modifier = Modifier,
    settings: Settings = Settings(),
    onSettingsChange: (Settings) -> Unit = {},
) {
    var sudoku by remember { mutableStateOf(Sudoku.puzzle(
        target = Difficulty.MEDIUM
    )) }
    var selected by remember { mutableStateOf<Pos?>(null) }
    var notesMode by remember { mutableStateOf(false) }
    var timer by remember { mutableStateOf(GameTimer()) }
    var isResumed by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    fun updateTimer() {
        val now = SystemClock.elapsedRealtime()
        timer = if (isResumed && !showSettings) timer.start(now) else timer.pause(now)
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
        selected?.let { sudoku = sudoku.with(it.row, it.col, cell) }
    }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (settings.showTimer) TimerText(timer)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { setShowSettings(true) }) {
                Icon(painterResource(R.drawable.ic_settings), contentDescription = "Settings")
            }
        }
        SudokuGrid(
            sudoku = sudoku,
            selected = selected,
            onCellClick = { selected = it },
            highlightRowAndColumn = settings.highlightRowAndColumn,
            highlightSameDigits = settings.highlightSameDigits,
        )
        NumberPad(
            notesMode = notesMode,
            onDigit = { d ->
                if (notesMode) {
                    selected?.let { sudoku = sudoku.toggleNote(it.row, it.col, Digit(d)) }
                } else {
                    setSelected(Cell.Filled(Digit(d)))
                }
            },
            onClear = { setSelected(Cell.Empty) },
            onToggleNotes = { notesMode = !notesMode },
        )
    }

    if (showSettings) {
        SettingsSheet(
            settings = settings,
            onSettingsChange = onSettingsChange,
            onDismiss = { setShowSettings(false) },
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
) {
    val lineColor = MaterialTheme.colorScheme.onSurface
    val selectedDigit = selected?.let { sudoku[it.row, it.col].digitOrNull }

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

@Composable
private fun NumberPad(
    notesMode: Boolean,
    onDigit: (Int) -> Unit,
    onClear: () -> Unit,
    onToggleNotes: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        (1..9).chunked(5).forEach { digits ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                digits.forEach { d ->
                    Button(onClick = { onDigit(d) }, modifier = Modifier.weight(1f)) {
                        Text(d.toString())
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = onClear, modifier = Modifier.weight(1f)) {
                Text("Clear")
            }
            if (notesMode) {
                FilledTonalButton(onClick = onToggleNotes, modifier = Modifier.weight(1f)) {
                    Text("Notes: on")
                }
            } else {
                OutlinedButton(onClick = onToggleNotes, modifier = Modifier.weight(1f)) {
                    Text("Notes: off")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SudokuScreenPreview() {
    SudokuTheme { SudokuScreen() }
}
