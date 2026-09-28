package com.maxer137.sudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.maxer137.sudoku.data.SettingsStore
import com.maxer137.sudoku.ui.SudokuScreen
import com.maxer137.sudoku.ui.theme.SudokuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val settingsStore = SettingsStore(applicationContext)
        setContent {
            var settings by remember { mutableStateOf(settingsStore.load()) }
            SudokuTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SudokuScreen(
                        modifier = Modifier.padding(innerPadding),
                        settings = settings,
                        onSettingsChange = {
                            settings = it
                            settingsStore.save(it)
                        },
                    )
                }
            }
        }
    }
}
