package com.maxer137.sudoku.data

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.random.Random

class PuzzleUniquenessTest {
    @Test
    fun easyPuzzles_haveOneSolution() = assertUnique(Difficulty.EASY)

    @Test
    fun mediumPuzzles_haveOneSolution() = assertUnique(Difficulty.MEDIUM)

    @Test
    fun hardPuzzles_haveOneSolution() = assertUnique(Difficulty.HARD)

    @Test
    fun countSolutions_findsSeveralForAnEmptyGrid() {
        assertEquals(2, countSolutions(IntArray(81)))
    }

    @Test
    fun countSolutions_findsNoneForAContradiction() {
        val grid = IntArray(81).also { it[0] = 1; it[1] = 1 }
        assertEquals(0, countSolutions(grid))
    }

    private fun assertUnique(difficulty: Difficulty) {
        for (seed in 1L..PUZZLES_PER_DIFFICULTY) {
            val sudoku = Sudoku.puzzle(difficulty, Random(seed))
            val grid = IntArray(81) { sudoku[it / 9, it % 9].digitOrNull?.value ?: 0 }
            assertEquals("$difficulty puzzle from seed $seed", 1, countSolutions(grid))
        }
    }

    private companion object {
        const val PUZZLES_PER_DIFFICULTY = 25
    }
}

/**
 * Counts the solutions of [grid] (81 cells, 0 = empty) by backtracking, stopping at [limit].
 * Always fills the cell with the fewest candidates first to keep the search small.
 */
private fun countSolutions(grid: IntArray, limit: Int = 2): Int {
    val cells = grid.copyOf()

    fun candidates(i: Int): Int {
        val row = i / 9
        val col = i % 9
        val boxRow = row / 3 * 3
        val boxCol = col / 3 * 3
        var used = 0
        for (k in 0 until 9) {
            used = used or (1 shl cells[row * 9 + k]) or (1 shl cells[k * 9 + col]) or
                (1 shl cells[(boxRow + k / 3) * 9 + boxCol + k % 3])
        }
        return 0x3FE and used.inv() // bits 1..9
    }

    fun search(): Int {
        var best = -1
        var bestCandidates = 0
        for (i in 0 until 81) if (cells[i] == 0) {
            val c = candidates(i)
            if (best == -1 || Integer.bitCount(c) < Integer.bitCount(bestCandidates)) {
                best = i
                bestCandidates = c
            }
        }
        if (best == -1) return 1 // no empty cells left: a solution
        var found = 0
        for (d in 1..9) if (bestCandidates and (1 shl d) != 0) {
            cells[best] = d
            found += search()
            cells[best] = 0
            if (found >= limit) break
        }
        return found
    }

    // a given that clashes with another has no solution, but the search would never notice
    for (i in 0 until 81) if (cells[i] != 0) {
        val d = cells[i]
        cells[i] = 0
        val allowed = candidates(i) and (1 shl d) != 0
        cells[i] = d
        if (!allowed) return 0
    }
    return search()
}
