package com.maxer137.sudoku.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.maxer137.sudoku.data.Sudoku
import com.maxer137.sudoku.ui.theme.SudokuTheme

@Composable
fun Sudoku(sudoku: Sudoku) {
    Text(
        text = "Hello!"
    )
}

@Preview(showBackground = true)
@Composable
fun SudokuPreview() {
    SudokuTheme {
        Sudoku(Sudoku.randomSolved())
    }
}

