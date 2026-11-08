package com.example.hiittimer

/** The four phases of a HIIT session. */
enum class Phase { GET_READY, WORK, REST, DONE }

/** Immutable snapshot of where the session is right now. */
data class RoundState(val phase: Phase, val round: Int)

/**
 * Pure session state machine — no Android dependencies, unit-testable.
 *
 * Session shape: GET_READY -> (WORK -> REST) x (rounds-1) -> WORK -> DONE.
 * Work finishes early only after the final round.
 */
object TimerLogic {

    fun next(state: RoundState, rounds: Int): RoundState = when (state.phase) {
        Phase.GET_READY -> RoundState(Phase.WORK, state.round)
        Phase.WORK -> if (state.round >= rounds) {
            RoundState(Phase.DONE, state.round)
        } else {
            RoundState(Phase.REST, state.round)
        }
        Phase.REST -> RoundState(Phase.WORK, state.round + 1)
        Phase.DONE -> state
    }

    fun durationSeconds(
        phase: Phase,
        workTime: Int,
        restTime: Int,
        getReadySeconds: Int,
    ): Int = when (phase) {
        Phase.GET_READY -> getReadySeconds
        Phase.WORK -> workTime
        Phase.REST -> restTime
        Phase.DONE -> 0
    }

    fun backgroundFor(phase: Phase): Int = when (phase) {
        Phase.GET_READY, Phase.WORK -> 0xFFFF5722.toInt()
        Phase.REST -> 0xFF4CAF50.toInt()
        Phase.DONE -> 0xFF2196F3.toInt()
    }

    fun labelFor(phase: Phase): String = when (phase) {
        Phase.GET_READY -> "GET READY"
        Phase.WORK -> "WORK"
        Phase.REST -> "REST"
        Phase.DONE -> "DONE"
    }
}
