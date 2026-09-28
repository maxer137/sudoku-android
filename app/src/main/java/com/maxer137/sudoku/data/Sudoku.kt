package com.maxer137.sudoku.data

import kotlin.random.Random

data class Digit(val value: Int) {
    init {
        require(value in 1..9) { "Value of cell must be 1..9, got $value"}
    }
}

sealed interface Cell {
    data object Empty: Cell
    data class Filled(val digit: Digit) : Cell
}

data class Sudoku(
    val data: List<List<Cell>>
) {
    init {
        require(data.size == SIZE && data.all { it.size == SIZE }) {
            "Sudoku must be ${SIZE}x$SIZE"
        }
    }

    operator fun get(row: Int, col: Int): Cell = data[row][col]

    fun with(row: Int, col: Int, cell: Cell): Sudoku =
        copy(data = data.mapIndexed { r, line ->
            if (r == row) line.mapIndexed { c, old -> if (c == col) cell else old } else line
        })

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
    }
}