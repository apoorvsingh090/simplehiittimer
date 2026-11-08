package com.example.hiittimer

import org.junit.Assert.assertEquals
import org.junit.Test

class TimerLogicTest {

    @Test
    fun getReadyLeadsToFirstWorkRound() {
        assertEquals(
            RoundState(Phase.WORK, 1),
            TimerLogic.next(RoundState(Phase.GET_READY, 1), rounds = 10),
        )
    }

    @Test
    fun workLeadsToRestBeforeFinalRound() {
        assertEquals(
            RoundState(Phase.REST, 3),
            TimerLogic.next(RoundState(Phase.WORK, 3), rounds = 10),
        )
    }

    @Test
    fun finalWorkRoundFinishesSession() {
        assertEquals(
            RoundState(Phase.DONE, 10),
            TimerLogic.next(RoundState(Phase.WORK, 10), rounds = 10),
        )
    }

    @Test
    fun restIncrementsRound() {
        assertEquals(
            RoundState(Phase.WORK, 4),
            TimerLogic.next(RoundState(Phase.REST, 3), rounds = 10),
        )
    }

    @Test
    fun doneIsTerminal() {
        val done = RoundState(Phase.DONE, 10)
        assertEquals(done, TimerLogic.next(done, rounds = 10))
    }

    @Test
    fun singleRoundSessionNeverRests() {
        assertEquals(
            RoundState(Phase.DONE, 1),
            TimerLogic.next(RoundState(Phase.WORK, 1), rounds = 1),
        )
    }

    @Test
    fun durationsMatchInputs() {
        assertEquals(5, TimerLogic.durationSeconds(Phase.GET_READY, 30, 15, 5))
        assertEquals(30, TimerLogic.durationSeconds(Phase.WORK, 30, 15, 5))
        assertEquals(15, TimerLogic.durationSeconds(Phase.REST, 30, 15, 5))
        assertEquals(0, TimerLogic.durationSeconds(Phase.DONE, 30, 15, 5))
    }
}
