package com.maxer137.sudoku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.maxer137.sudoku.data.Difficulty
import com.maxer137.sudoku.data.GameStore
import com.maxer137.sudoku.data.SavedGame
import com.maxer137.sudoku.data.SettingsStore
import com.maxer137.sudoku.ui.MainMenu
import com.maxer137.sudoku.ui.SudokuScreen
import com.maxer137.sudoku.ui.theme.SudokuTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val settingsStore = SettingsStore(applicationContext)
        val gameStore = GameStore(applicationContext)
        setContent {
            var settings by remember { mutableStateOf(settingsStore.load()) }
            var game by remember { mutableStateOf(gameStore.load()) }
            var inGame by rememberSaveable { mutableStateOf(game?.sudoku?.isSolved == false) }
            var generating by remember { mutableStateOf<Difficulty?>(null) }
            val scope = rememberCoroutineScope()
            val updateGame = { new: SavedGame ->
                game = new
                gameStore.save(new)
            }
            SudokuTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val current = game
                    if (inGame && current != null) {
                        BackHandler { inGame = false }
                        key(current.difficulty, current.seed) {
                            SudokuScreen(
                                game = current,
                                modifier = Modifier.padding(innerPadding),
                                onGameChange = updateGame,
                                onExit = { inGame = false },
                                settings = settings,
                                onSettingsChange = {
                                    settings = it
                                    settingsStore.save(it)
                                },
                            )
                        }
                    } else {
                        MainMenu(
                            onStart = { difficulty ->
                                if (generating == null) {
                                    generating = difficulty
                                    scope.launch {
                                        val seed = gameStore.nextSeed(difficulty)
                                        val new = withContext(Dispatchers.Default) {
                                            SavedGame.new(difficulty, seed)
                                        }
                                        updateGame(new)
                                        inGame = true
                                        generating = null
                                    }
                                }
                            },
                            modifier = Modifier.padding(innerPadding),
                            generating = generating,
                            continueDifficulty = current?.takeUnless { it.sudoku.isSolved }?.difficulty,
                            onContinue = { inGame = true },
                        )
                    }
                }
            }
        }
    }
}
