package com.maxer137.sudoku.data

import org.junit.Assert.assertEquals
import org.junit.Test

class SudokuConflictsTest {
    private fun filled(value: Int) = Cell.Filled(Digit(value))

    @Test
    fun conflicts_emptyGridHasNone() {
        assertEquals(emptySet<Pos>(), Sudoku.empty().conflicts())
    }

    @Test
    fun conflicts_sameDigitInRow() {
        val sudoku = Sudoku.empty().with(4, 0, filled(2)).with(4, 8, filled(2))
        assertEquals(setOf(Pos(4, 0), Pos(4, 8)), sudoku.conflicts())
    }

    @Test
    fun conflicts_sameDigitInColumn() {
        val sudoku = Sudoku.empty().with(0, 3, filled(7)).with(8, 3, filled(7))
        assertEquals(setOf(Pos(0, 3), Pos(8, 3)), sudoku.conflicts())
    }

    @Test
    fun conflicts_sameDigitInBox() {
        val sudoku = Sudoku.empty().with(0, 0, filled(5)).with(2, 2, filled(5))
        assertEquals(setOf(Pos(0, 0), Pos(2, 2)), sudoku.conflicts())
    }

    @Test
    fun conflicts_differentDigitsDontConflict() {
        val sudoku = Sudoku.empty().with(0, 0, filled(1)).with(0, 1, filled(2))
        assertEquals(emptySet<Pos>(), sudoku.conflicts())
    }

    @Test
    fun conflicts_sameDigitInUnrelatedCellsIsFine() {
        val sudoku = Sudoku.empty().with(0, 0, filled(3)).with(4, 4, filled(3))
        assertEquals(emptySet<Pos>(), sudoku.conflicts())
    }

    @Test
    fun conflicts_includesGivens() {
        val sudoku = Sudoku(List(Sudoku.SIZE) { r ->
            List(Sudoku.SIZE) { c -> if (r == 0 && c == 0) Cell.Given(Digit(9)) else Cell.Empty }
        }).with(0, 5, filled(9))
        assertEquals(setOf(Pos(0, 0), Pos(0, 5)), sudoku.conflicts())
    }

    @Test
    fun conflicts_ignoresNotes() {
        val sudoku = Sudoku.empty().with(0, 0, filled(4)).toggleNote(0, 1, Digit(4))
        assertEquals(emptySet<Pos>(), sudoku.conflicts())
    }
}
