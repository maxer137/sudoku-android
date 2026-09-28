package com.maxer137.sudoku.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maxer137.sudoku.data.Cell
import com.maxer137.sudoku.data.Difficulty
import com.maxer137.sudoku.data.Digit
import com.maxer137.sudoku.data.Pos
import com.maxer137.sudoku.data.Sudoku
import com.maxer137.sudoku.data.digitOrNull
import com.maxer137.sudoku.ui.theme.SudokuTheme

@Composable
fun SudokuScreen(modifier: Modifier = Modifier) {
    var sudoku by remember { mutableStateOf(Sudoku.puzzle(
        target = Difficulty.MEDIUM
    )) }
    var selected by remember { mutableStateOf<Pos?>(null) }
    var notesMode by remember { mutableStateOf(false) }

    fun setSelected(cell: Cell) {
        selected?.let { sudoku = sudoku.with(it.row, it.col, cell) }
    }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SudokuGrid(
            sudoku = sudoku,
            selected = selected,
            onCellClick = { selected = it },
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
}

@Composable
fun SudokuGrid(
    sudoku: Sudoku,
    selected: Pos?,
    onCellClick: (Pos) -> Unit,
    modifier: Modifier = Modifier,
) {
    val lineColor = MaterialTheme.colorScheme.onSurface

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
                                    SudokuCell(
                                        cell = sudoku[row, col],
                                        isSelected = pos == selected,
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

@Composable
private fun SudokuCell(
    cell: Cell,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .border(0.5.dp, MaterialTheme.colorScheme.outline)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primaryContainer
                    cell is Cell.Given -> MaterialTheme.colorScheme.secondaryContainer
                    else -> Color.Transparent
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (cell is Cell.Notes) {
            NotesGrid(cell.digits)
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
private fun NotesGrid(digits: Set<Digit>, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxSize().padding(1.dp)) {
        for (r in 0 until 3) {
            Row(Modifier.weight(1f)) {
                for (c in 0 until 3) {
                    val digit = Digit(r * 3 + c + 1)
                    Box(Modifier.weight(1f).fillMaxHeight(), contentAlignment = Alignment.Center) {
                        if (digit in digits) {
                            Text(
                                text = digit.value.toString(),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
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
