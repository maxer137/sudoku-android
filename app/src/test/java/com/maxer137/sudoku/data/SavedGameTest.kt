package com.maxer137.sudoku.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

private fun Sudoku.toIntArray() = data.flatten().map { it.digitOrNull?.value ?: 0 }.toIntArray()

class SavedGameTest {
    @Test
    fun dailySeed_firstPuzzleIsLevelAndDate() {
        assertEquals("120260928", dailySeed("20260928", Difficulty.EASY, 0))
        assertEquals("320260928", dailySeed("20260928", Difficulty.HARD, 0))
    }

    @Test
    fun dailySeed_laterPuzzlesAppendTheirIndex() {
        assertEquals("2202609281", dailySeed("20260928", Difficulty.MEDIUM, 1))
        assertEquals("22026092812", dailySeed("20260928", Difficulty.MEDIUM, 12))
    }

    @Test
    fun new_difficultiesOfTheSameDayHaveDifferentSolutions() {
        val date = "20260928"
        val solutions = Difficulty.entries.map { difficulty ->
            LogicSolver(SavedGame.new(difficulty, dailySeed(date, difficulty, 0)).sudoku.toIntArray())
                .apply { rate(difficulty.level) }
                .values.toList()
        }
        assertEquals(solutions.size, solutions.toSet().size)
    }

    @Test
    fun new_sameSeedGivesSamePuzzle() {
        val a = SavedGame.new(Difficulty.EASY, "20260928")
        val b = SavedGame.new(Difficulty.EASY, "20260928")
        assertEquals(a.sudoku, b.sudoku)
    }

    @Test
    fun new_differentSeedGivesDifferentPuzzle() {
        val a = SavedGame.new(Difficulty.EASY, "20260928")
        val b = SavedGame.new(Difficulty.EASY, "202609281")
        assertNotEquals(a.sudoku, b.sudoku)
    }

    @Test
    fun encode_roundTripsEveryCellKind() {
        val sudoku = Sudoku(List(Sudoku.SIZE) { List(Sudoku.SIZE) { Cell.Empty } })
            .with(0, 0, Cell.Given(Digit(5)))
            .with(0, 1, Cell.Filled(Digit(9)))
            .with(8, 8, Cell.Notes(setOf(Digit(1), Digit(3), Digit(7))))
        assertEquals(sudoku, Sudoku.decode(sudoku.encode()))
    }

    @Test
    fun encode_roundTripsAGeneratedPuzzle() {
        val sudoku = SavedGame.new(Difficulty.EASY, "20260928").sudoku
        assertEquals(sudoku, Sudoku.decode(sudoku.encode()))
    }
}
