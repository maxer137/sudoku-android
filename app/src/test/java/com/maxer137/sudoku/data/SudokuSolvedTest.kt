package com.maxer137.sudoku.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class SudokuSolvedTest {
    private val solved = Sudoku.randomSolved(Random(1))

    @Test
    fun emptyGrid_isNotFullOrSolved() {
        assertFalse(Sudoku.empty().isFull)
        assertFalse(Sudoku.empty().isSolved)
    }

    @Test
    fun validGrid_isSolved() {
        assertTrue(solved.isFull)
        assertTrue(solved.isSolved)
    }

    @Test
    fun gridWithOneEmptyCell_isNotFull() {
        val sudoku = solved.with(4, 4, Cell.Empty)
        assertFalse(sudoku.isFull)
        assertFalse(sudoku.isSolved)
    }

    @Test
    fun gridWithNotes_isNotFull() {
        val sudoku = solved.with(4, 4, Cell.Notes(setOf(Digit(1))))
        assertFalse(sudoku.isFull)
    }

    @Test
    fun fullGridWithAMistake_isNotSolved() {
        val digit = solved[0, 0].digitOrNull!!.value
        val wrong = Digit(digit % 9 + 1)
        val sudoku = solved.with(0, 0, Cell.Filled(wrong))
        assertTrue(sudoku.isFull)
        assertFalse(sudoku.isSolved)
    }
}
