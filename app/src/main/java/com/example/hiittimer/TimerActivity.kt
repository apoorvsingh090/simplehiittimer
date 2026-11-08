package com.example.hiittimer

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.hiittimer.databinding.ActivityTimerBinding
import java.util.Locale

class TimerActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    companion object {
        private const val TAG = "TimerActivity"
        private const val GET_READY_SECONDS = 5
        private const val TICK_MS = 1000L
        private const val FINISH_DELAY_MS = 3000L
    }

    private lateinit var binding: ActivityTimerBinding
    private var workTime = 30
    private var restTime = 30
    private var rounds = 10
    private var startDelay = true
    private var voiceCoach = false

    private var state = RoundState(Phase.WORK, 1)
    private var isPaused = false
    private var remainingMs = 0L
    private var phaseEndsAt = 0L
    private var countDownTimer: CountDownTimer? = null
    private var finishRunnable: Runnable? = null
    private var released = false

    private lateinit var toneGenerator: ToneGenerator
    private var tts: TextToSpeech? = null
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        workTime = intent.getIntExtra("workTime", 30)
        restTime = intent.getIntExtra("restTime", 30)
        rounds = intent.getIntExtra("rounds", 10)
        startDelay = intent.getBooleanExtra("startDelay", true)
        voiceCoach = intent.getBooleanExtra("voiceCoach", false)

        toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
        tts = TextToSpeech(this, this)

        setupButtons()

        if (savedInstanceState != null) {
            restore(savedInstanceState)
        } else if (startDelay) {
            beginPhase(Phase.GET_READY, 1)
        } else {
            beginPhase(Phase.WORK, 1)
        }
    }

    private fun setupButtons() {
        binding.btnPauseResume.setOnClickListener {
            if (isPaused) resumeTimer() else pauseTimer()
        }

        binding.btnStop.setOnClickListener {
            stopTimer()
            finish()
        }
    }

    private fun beginPhase(phase: Phase, round: Int) {
        state = RoundState(phase, round)
        renderPhase()

        if (phase == Phase.DONE) {
            binding.tvTimer.text = String.format(Locale.US, "%02d", 0)
            playBeep()
            speakIfEnabled("workout complete")
            finishRunnable = Runnable { finish() }.also {
                handler.postDelayed(it, FINISH_DELAY_MS)
            }
            return
        }

        when (phase) {
            Phase.WORK -> speakIfEnabled("work")
            Phase.REST -> speakIfEnabled("rest")
            Phase.GET_READY -> speakIfEnabled("get ready")
            Phase.DONE -> Unit
        }

        startCountdownTimer(
            TimerLogic.durationSeconds(phase, workTime, restTime, GET_READY_SECONDS) * 1000L
        )
    }

    /** Start (or resume) ticking down [durationMs] for the current phase. */
    private fun startCountdownTimer(durationMs: Long) {
        countDownTimer?.cancel()
        remainingMs = durationMs
        phaseEndsAt = SystemClock.elapsedRealtime() + durationMs
        renderRemaining(durationMs)
        countDownTimer = object : CountDownTimer(durationMs, TICK_MS) {
            override fun onTick(millisUntilFinished: Long) {
                remainingMs = millisUntilFinished
                renderRemaining(millisUntilFinished)
                val secondsLeft = (millisUntilFinished / 1000).toInt()
                if (secondsLeft in 1..3) playBeep()
            }

            override fun onFinish() {
                // Only reached when the clock actually elapsed (pausing cancels
                // the timer), so a paused workout can never advance or finish.
                remainingMs = 0
                playBeep()
                advance()
            }
        }.start()
    }

    private fun advance() {
        val next = TimerLogic.next(state, rounds)
        beginPhase(next.phase, next.round)
    }

    private fun renderPhase() {
        binding.tvPhase.text = TimerLogic.labelFor(state.phase)
        binding.tvRoundInfo.text = if (state.phase == Phase.DONE) {
            "Complete!"
        } else {
            "Round ${state.round} / $rounds"
        }
        binding.root.setBackgroundColor(TimerLogic.backgroundFor(state.phase))
    }

    private fun renderRemaining(millis: Long) {
        val seconds = (millis / 1000).toInt()
        binding.tvTimer.text = String.format(Locale.US, "%02d", seconds)
    }

    private fun pauseTimer() {
        if (isPaused || state.phase == Phase.DONE) return
        isPaused = true
        remainingMs = (phaseEndsAt - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
        countDownTimer?.cancel()
        countDownTimer = null
        renderRemaining(remainingMs)
        binding.btnPauseResume.text = "RESUME"
    }

    private fun resumeTimer() {
        if (!isPaused) return
        isPaused = false
        binding.btnPauseResume.text = "PAUSE"
        if (remainingMs > 0L) {
            startCountdownTimer(remainingMs)
        } else {
            advance()
        }
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        countDownTimer = null
        finishRunnable?.let { handler.removeCallbacks(it) }
        finishRunnable = null
        if (!released) {
            released = true
            toneGenerator.release()
            tts?.shutdown()
        }
    }

    private fun playBeep() {
        try {
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
        } catch (e: IllegalStateException) {
            Log.w(TAG, "beep failed", e)
        }
    }

    private fun speakIfEnabled(text: String) {
        if (voiceCoach && tts != null) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
        }
    }

    override fun onPause() {
        super.onPause()
        // A backgrounded timer would keep beeping with no way to stop it.
        if (state.phase != Phase.DONE) pauseTimer()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("phase", state.phase.name)
        outState.putInt("round", state.round)
        outState.putLong("remainingMs", remainingMs)
        outState.putBoolean("isPaused", isPaused)
        outState.putInt("workTime", workTime)
        outState.putInt("restTime", restTime)
        outState.putInt("rounds", rounds)
        outState.putBoolean("startDelay", startDelay)
        outState.putBoolean("voiceCoach", voiceCoach)
    }

    private fun restore(saved: Bundle) {
        workTime = saved.getInt("workTime", workTime)
        restTime = saved.getInt("restTime", restTime)
        rounds = saved.getInt("rounds", rounds)
        startDelay = saved.getBoolean("startDelay", startDelay)
        voiceCoach = saved.getBoolean("voiceCoach", voiceCoach)

        val phase = runCatching {
            Phase.valueOf(saved.getString("phase") ?: Phase.WORK.name)
        }.getOrDefault(Phase.WORK)
        state = RoundState(phase, saved.getInt("round", 1))
        remainingMs = saved.getLong("remainingMs", 0L)
        isPaused = saved.getBoolean("isPaused", false)

        renderPhase()
        renderRemaining(remainingMs)
        if (phase == Phase.DONE) {
            return
        }
        if (isPaused || remainingMs <= 0L) {
            isPaused = true
            binding.btnPauseResume.text = "RESUME"
        } else {
            startCountdownTimer(remainingMs)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()
    }
}
