package com.maxer137.sudoku.data

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

class SudokuCompletedDigitsTest {
    private val solved = Sudoku.randomSolved(Random(1))

    @Test
    fun emptyGrid_hasNoCompletedDigits() {
        assertEquals(emptySet<Digit>(), Sudoku.empty().completedDigits())
    }

    @Test
    fun solvedGrid_hasAllDigitsCompleted() {
        assertEquals((1..9).map { Digit(it) }.toSet(), solved.completedDigits())
    }

    @Test
    fun clearingACell_uncompletesItsDigit() {
        val digit = solved[0, 0].digitOrNull!!
        val sudoku = solved.with(0, 0, Cell.Empty)
        assertEquals((1..9).map { Digit(it) }.toSet() - digit, sudoku.completedDigits())
    }
}
