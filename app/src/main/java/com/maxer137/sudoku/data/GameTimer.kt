package com.maxer137.sudoku.data

/**
 * Play time that only counts while the player is actually working on the puzzle.
 *
 * Nothing ticks: the timer just remembers when the current running stretch began
 * ([runningSince]) and how much time earlier stretches added up to ([accumulatedMs]).
 * The elapsed time is computed on demand from a monotonic clock, so a paused or
 * hidden timer costs nothing.
 *
 * All timestamps are milliseconds from a monotonic clock
 * (e.g. `SystemClock.elapsedRealtime()`), never wall-clock time.
 */
data class GameTimer(
    val accumulatedMs: Long = 0,
    val runningSince: Long? = null,
) {
    val isRunning: Boolean
        get() = runningSince != null

    /** Starts (or resumes) counting from [now]. No-op if already running. */
    fun start(now: Long): GameTimer =
        if (isRunning) this else copy(runningSince = now)

    /** Stops counting at [now], banking the current stretch. No-op if already paused. */
    fun pause(now: Long): GameTimer =
        if (runningSince == null) this
        else GameTimer(accumulatedMs = elapsedMs(now), runningSince = null)

    /** Total active time up to [now]. */
    fun elapsedMs(now: Long): Long =
        accumulatedMs + (runningSince?.let { (now - it).coerceAtLeast(0) } ?: 0)
}
