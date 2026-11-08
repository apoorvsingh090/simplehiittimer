package com.example.hiittimer

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.hiittimer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: SharedPreferences

    private var workTime = 30
    private var restTime = 30
    private var rounds = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Simple HIIT Timer"

        prefs = getSharedPreferences("HiitTimerPrefs", MODE_PRIVATE)
        loadSettings()

        setupButtons()
        updateDisplays()
    }

    private fun loadSettings() {
        workTime = prefs.getInt("workTime", 30)
        restTime = prefs.getInt("restTime", 30)
        rounds = prefs.getInt("rounds", 10)
        binding.cbStartDelay.isChecked = prefs.getBoolean("startDelay", true)
        binding.cbVoiceCoach.isChecked = prefs.getBoolean("voiceCoach", false)
    }

    private fun saveSettings() {
        prefs.edit().apply {
            putInt("workTime", workTime)
            putInt("restTime", restTime)
            putInt("rounds", rounds)
            putBoolean("startDelay", binding.cbStartDelay.isChecked)
            putBoolean("voiceCoach", binding.cbVoiceCoach.isChecked)
            apply()
        }
    }

    private fun setupButtons() {
        binding.btnWorkMinus.setOnClickListener {
            if (workTime > 1) {
                workTime--
                updateDisplays()
                saveSettings()
            }
        }
        binding.btnWorkPlus.setOnClickListener {
            if (workTime < 999) {
                workTime++
                updateDisplays()
                saveSettings()
            }
        }
        binding.btnRestMinus.setOnClickListener {
            if (restTime > 1) {
                restTime--
                updateDisplays()
                saveSettings()
            }
        }
        binding.btnRestPlus.setOnClickListener {
            if (restTime < 999) {
                restTime++
                updateDisplays()
                saveSettings()
            }
        }
        binding.btnRoundsMinus.setOnClickListener {
            if (rounds > 1) {
                rounds--
                updateDisplays()
                saveSettings()
            }
        }
        binding.btnRoundsPlus.setOnClickListener {
            if (rounds < 50) {
                rounds++
                updateDisplays()
                saveSettings()
            }
        }

        binding.btnPreset1.setOnClickListener { applyPreset(30, 30, 10) }
        binding.btnPreset2.setOnClickListener { applyPreset(40, 20, 8) }
        binding.btnPreset3.setOnClickListener { applyPreset(20, 10, 8) }
        binding.btnPreset4.setOnClickListener { applyPreset(60, 60, 10) }
        binding.btnPreset5.setOnClickListener { applyPreset(60, 120, 8) }
        binding.btnPreset6.setOnClickListener { applyPreset(15, 45, 10) }

        binding.btnStart.setOnClickListener {
            saveSettings()
            val intent = Intent(this, TimerActivity::class.java).apply {
                putExtra("workTime", workTime)
                putExtra("restTime", restTime)
                putExtra("rounds", rounds)
                putExtra("startDelay", binding.cbStartDelay.isChecked)
                putExtra("voiceCoach", binding.cbVoiceCoach.isChecked)
            }
            startActivity(intent)
        }
    }

    private fun applyPreset(w: Int, r: Int, roundsCount: Int) {
        workTime = w
        restTime = r
        rounds = roundsCount
        updateDisplays()
        saveSettings()
    }

    private fun updateDisplays() {
        binding.tvWorkTime.text = workTime.toString()
        binding.tvRestTime.text = restTime.toString()
        binding.tvRounds.text = rounds.toString()
    }
}
