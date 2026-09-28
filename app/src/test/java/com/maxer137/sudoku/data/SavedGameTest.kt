package com.maxer137.sudoku.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class SavedGameTest {
    @Test
    fun dailySeed_firstPuzzleIsJustTheDate() {
        assertEquals("20260928", dailySeed("20260928", 0))
    }

    @Test
    fun dailySeed_laterPuzzlesAppendTheirIndex() {
        assertEquals("202609281", dailySeed("20260928", 1))
        assertEquals("2026092812", dailySeed("20260928", 12))
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
