package com.maxer137.sudoku.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameTimerTest {
    @Test
    fun newTimer_isPausedAtZero() {
        val timer = GameTimer()
        assertFalse(timer.isRunning)
        assertEquals(0, timer.elapsedMs(now = 5_000))
    }

    @Test
    fun runningTimer_countsFromStart() {
        val timer = GameTimer().start(now = 1_000)
        assertTrue(timer.isRunning)
        assertEquals(2_500, timer.elapsedMs(now = 3_500))
    }

    @Test
    fun pausedTimer_ignoresTimeWhilePaused() {
        val timer = GameTimer()
            .start(now = 0)
            .pause(now = 1_000)
            .start(now = 60_000)
            .pause(now = 62_000)
        assertEquals(3_000, timer.elapsedMs(now = 1_000_000))
    }

    @Test
    fun start_whenRunning_keepsOriginalStart() {
        val timer = GameTimer().start(now = 0).start(now = 500)
        assertEquals(1_000, timer.elapsedMs(now = 1_000))
    }

    @Test
    fun pause_whenPaused_changesNothing() {
        val timer = GameTimer().start(now = 0).pause(now = 1_000)
        assertEquals(timer, timer.pause(now = 9_000))
    }
}
