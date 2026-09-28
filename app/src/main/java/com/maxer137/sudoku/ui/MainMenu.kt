package com.maxer137.sudoku.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.maxer137.sudoku.data.Difficulty
import com.maxer137.sudoku.ui.theme.SudokuTheme

@Composable
fun MainMenu(
    onStart: (Difficulty) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Sudoku", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(16.dp))
        Difficulty.entries.forEach { difficulty ->
            Button(onClick = { onStart(difficulty) }, modifier = Modifier.fillMaxWidth()) {
                Text(difficulty.label)
            }
        }
    }
}

private val Difficulty.label: String
    get() = when (this) {
        Difficulty.EASY -> "Easy"
        Difficulty.MEDIUM -> "Medium"
        Difficulty.HARD -> "Hard"
    }

@Preview(showBackground = true)
@Composable
private fun MainMenuPreview() {
    SudokuTheme { MainMenu(onStart = {}) }
}
