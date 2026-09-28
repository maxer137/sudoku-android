package com.maxer137.sudoku.data

import org.junit.Assert.assertEquals
import org.junit.Test

class SudokuNotesTest {
    private val two = Digit(2)
    private val three = Digit(3)

    @Test
    fun toggleNote_addsCandidatesToEmptyCell() {
        val sudoku = Sudoku.empty().toggleNote(0, 0, two).toggleNote(0, 0, three)
        assertEquals(Cell.Notes(setOf(two, three)), sudoku[0, 0])
    }

    @Test
    fun toggleNote_removesExistingCandidate() {
        val sudoku = Sudoku.empty()
            .toggleNote(0, 0, two)
            .toggleNote(0, 0, three)
            .toggleNote(0, 0, two)
        assertEquals(Cell.Notes(setOf(three)), sudoku[0, 0])
    }

    @Test
    fun toggleNote_removingLastCandidateEmptiesCell() {
        val sudoku = Sudoku.empty().toggleNote(0, 0, two).toggleNote(0, 0, two)
        assertEquals(Cell.Empty, sudoku[0, 0])
    }

    @Test
    fun toggleNote_replacesFilledDigit() {
        val sudoku = Sudoku.empty().with(0, 0, Cell.Filled(Digit(5))).toggleNote(0, 0, two)
        assertEquals(Cell.Notes(setOf(two)), sudoku[0, 0])
    }

    @Test
    fun toggleNote_ignoresGivenCell() {
        val given = Cell.Given(Digit(5))
        val sudoku = Sudoku(List(Sudoku.SIZE) { r ->
            List(Sudoku.SIZE) { c -> if (r == 0 && c == 0) given else Cell.Empty }
        }).toggleNote(0, 0, two)
        assertEquals(given, sudoku[0, 0])
    }
}
