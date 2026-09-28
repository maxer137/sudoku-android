package com.maxer137.sudoku.data

import kotlin.random.Random

data class Pos(val row: Int, val col: Int)

data class Digit(val value: Int) {
    init {
        require(value in 1..9) { "Value of cell must be 1..9, got $value"}
    }
}

sealed interface Cell {
    data object Empty: Cell
    data class Filled(val digit: Digit) : Cell
    data class Given(val digit: Digit) : Cell
}

val Cell.digitOrNull: Digit?
    get() = when (this) {
        Cell.Empty -> null
        is Cell.Filled -> digit
        is Cell.Given -> digit
    }

val Cell.isEditable: Boolean
    get() = this !is Cell.Given

data class Sudoku(
    val data: List<List<Cell>>
) {
    init {
        require(data.size == SIZE && data.all { it.size == SIZE }) {
            "Sudoku must be ${SIZE}x$SIZE"
        }
    }

    operator fun get(row: Int, col: Int): Cell = data[row][col]

    fun with(row: Int, col: Int, cell: Cell): Sudoku {
        if (!this[row, col].isEditable) return this   // locked, ignore
        return copy(data = data.mapIndexed { r, line ->
            if (r == row) line.mapIndexed { c, old -> if (c == col) cell else old } else line
        })
    }

    companion object {
        const val SIZE = 9

        fun empty(): Sudoku =
            Sudoku(List(SIZE) { List(SIZE) { Cell.Empty } })

        /** Every cell gets a random digit. Almost certainly NOT a valid sudoku. */
        fun random(random: Random = Random.Default): Sudoku =
            Sudoku(List(SIZE) {
                List(SIZE) { Cell.Filled(Digit(random.nextInt(1, 10))) } // upper bound is exclusive
            })

        /** A random valid solved grid. */
        fun randomSolved(random: Random = Random.Default): Sudoku {
            val digits = (1..9).shuffled(random)

            // shuffle the 3 bands, and the rows within each band (same for columns)
            fun order() = (0..2).shuffled(random).flatMap { g ->
                (0..2).shuffled(random).map { g * 3 + it }
            }

            val rows = order()
            val cols = order()
            return Sudoku(rows.map { r ->
                cols.map { c ->
                    Cell.Filled(Digit(digits[(3 * (r % 3) + r / 3 + c) % 9]))
                }
            })
        }

        fun puzzle(target: Difficulty, random: Random = Random.Default): Sudoku {
            while (true) {
                val solved = randomSolved(random)
                val grid = IntArray(81) { solved[it / SIZE, it % SIZE].digitOrNull?.value ?: 0 }

                for (i in (0 until 81).shuffled(random)) {
                    val d = grid[i]
                    grid[i] = 0
                    // keep the removal only if a solver capped at the target tier still finishes it
                    if (LogicSolver(grid).rate(target.level) == null) grid[i] = d
                }

                // floor check: the puzzle must actually need the target tier
                if (LogicSolver(grid).rate(target.level) != target.level) continue

                return Sudoku(List(SIZE) { r ->
                    List(SIZE) { c ->
                        val v = grid[r * SIZE + c]
                        if (v != 0) Cell.Given(Digit(v)) else Cell.Empty
                    }
                })
            }
        }
    }
}