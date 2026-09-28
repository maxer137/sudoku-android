package com.maxer137.sudoku.data

/**
 * [level] is the hardest technique tier a puzzle may need. [minGivens] stops clue removal early.
 */
enum class Difficulty(val level: Int, val minGivens: Int) { EASY(1, 36), MEDIUM(2, 30), HARD(3, 0) }

private val ALL = 0x1FF
private fun bit(d: Int) = 1 shl (d - 1)

private val ROWS  = List(9) { r -> IntArray(9) { c -> r * 9 + c } }
private val COLS  = List(9) { c -> IntArray(9) { r -> r * 9 + c } }
private val BOXES = List(9) { b -> IntArray(9) { k -> (b / 3 * 3 + k / 3) * 9 + (b % 3 * 3 + k % 3) } }
private val UNITS = ROWS + COLS + BOXES
private val PEERS = Array(81) { i ->
    UNITS.filter { i in it }.flatMap { it.toList() }.filter { it != i }.distinct().toIntArray()
}

class LogicSolver(puzzle: IntArray) {
    val values = puzzle.copyOf()
    private val cand = IntArray(81)

    init {
        for (i in 0 until 81) if (values[i] == 0) {
            cand[i] = ALL
            for (p in PEERS[i]) if (values[p] != 0) cand[i] = cand[i] and bit(values[p]).inv()
        }
    }

    private fun place(i: Int, d: Int) {
        values[i] = d; cand[i] = 0
        for (p in PEERS[i]) cand[p] = cand[p] and bit(d).inv()
    }

    private fun eliminate(i: Int, d: Int): Boolean {
        if (cand[i] and bit(d) == 0) return false
        cand[i] = cand[i] and bit(d).inv(); return true
    }

    // Level 1
    private fun nakedSingle(): Boolean {
        for (i in 0 until 81) if (values[i] == 0 && Integer.bitCount(cand[i]) == 1) {
            place(i, Integer.numberOfTrailingZeros(cand[i]) + 1); return true
        }
        return false
    }

    private fun hiddenSingle(): Boolean {
        for (u in UNITS) for (d in 1..9) {
            val spots = u.filter { values[it] == 0 && cand[it] and bit(d) != 0 }
            if (spots.size == 1 && u.none { values[it] == d }) { place(spots[0], d); return true }
        }
        return false
    }

    // Level 2
    private fun nakedPair(): Boolean {
        var changed = false
        for (u in UNITS) {
            val pairs = u.filter { values[it] == 0 && Integer.bitCount(cand[it]) == 2 }
            for (a in pairs.indices) for (b in a + 1 until pairs.size) {
                val m = cand[pairs[a]]
                if (m != cand[pairs[b]]) continue
                for (o in u) if (o != pairs[a] && o != pairs[b] && values[o] == 0) {
                    for (d in 1..9) if (m and bit(d) != 0 && eliminate(o, d)) changed = true
                }
            }
        }
        return changed
    }

    // pointing pairs + box/line reduction
    private fun lockedCandidates(): Boolean {
        var changed = false
        for (box in BOXES) for (d in 1..9) {
            val cells = box.filter { cand[it] and bit(d) != 0 }
            if (cells.size < 2) continue
            val line = when {
                cells.all { it / 9 == cells[0] / 9 } -> ROWS[cells[0] / 9]
                cells.all { it % 9 == cells[0] % 9 } -> COLS[cells[0] % 9]
                else -> null
            } ?: continue
            for (o in line) if (o !in box && eliminate(o, d)) changed = true
        }
        for (line in ROWS + COLS) for (d in 1..9) {
            val cells = line.filter { cand[it] and bit(d) != 0 }
            if (cells.size < 2) continue
            val boxIdx = (cells[0] / 9 / 3) * 3 + (cells[0] % 9 / 3)
            if (cells.all { (it / 9 / 3) * 3 + (it % 9 / 3) == boxIdx })
                for (o in BOXES[boxIdx]) if (o !in line && eliminate(o, d)) changed = true
        }
        return changed
    }

    // Level 3
    private fun xWing(): Boolean {
        var changed = false
        for (lines in listOf(ROWS, COLS)) for (d in 1..9) {
            // for each line, positions (0..8) where d is still a candidate
            val pos = lines.map { l -> (0 until 9).filter { cand[l[it]] and bit(d) != 0 } }
            for (a in 0 until 9) {
                if (pos[a].size != 2) continue
                for (b in a + 1 until 9) if (pos[b] == pos[a]) {
                    for (o in 0 until 9) if (o != a && o != b)
                        for (p in pos[a]) if (eliminate(lines[o][p], d)) changed = true
                }
            }
        }
        return changed
    }

    private val techniques: List<Pair<Int, () -> Boolean>> = listOf(
        1 to ::nakedSingle, 1 to ::hiddenSingle,
        2 to ::nakedPair,   2 to ::lockedCandidates,
        3 to ::xWing,
        // add XY-Wing, Swordfish, coloring... as level 3
    )

    /** Returns the hardest level needed, or null if stuck within maxLevel. */
    fun rate(maxLevel: Int): Int? {
        var hardest = 1
        while (values.any { it == 0 }) {
            val step = techniques.firstOrNull { (lvl, t) -> lvl <= maxLevel && t() } ?: return null
            hardest = maxOf(hardest, step.first)
        }
        return hardest
    }
}