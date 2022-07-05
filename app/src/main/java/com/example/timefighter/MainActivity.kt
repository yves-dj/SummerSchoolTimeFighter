package com.example.timefighter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    private lateinit var scoreView: TextView
    private lateinit var timerView: TextView
    private lateinit var tapButton: Button

    private var score = 0

    private lateinit var countDownTimer: CountDownTimer
    private val initialStartTimeInMillis = 50000L
    private val countDownIntervalInMillis = 1000L
    private var timeLeftInMillis : Long = initialStartTimeInMillis

    private var isGameStarted = false

    companion object {
        private val TAG = MainActivity::class.java.simpleName
        private const val SCORE_KEY = "SCORE_KEY"
        private const val TIME_LEFT_KEY = "TIME_LEFT_KEY"
        private const val IS_GAME_STARTED = "IS_GAME_STARTED"
    }

    private lateinit var animScore: Animation
    private lateinit var animTime: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scoreView = findViewById(R.id.scoreView)
        timerView = findViewById(R.id.timerValue)
        tapButton = findViewById(R.id.tapButton)

        scoreView.text = getString(R.string.score, score)
        timerView.text = getString(R.string.timerValue, initialStartTimeInMillis / 1000)

        animScore = AnimationUtils.loadAnimation(this, R.anim.bounce)
        animTime = AnimationUtils.loadAnimation(this, R.anim.blink)

        tapButton.setOnClickListener {
            if (!isGameStarted) {
                startGame()
            }
            scoreView.startAnimation(animScore)
            incrementScore()
        }

        if (savedInstanceState != null) {
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeftInMillis = savedInstanceState.getLong(TIME_LEFT_KEY)
            isGameStarted = savedInstanceState.getBoolean(IS_GAME_STARTED)

            restoreGame()
        } else {
            resetGame()
        }
    }

    private fun restoreGame() {
        scoreView.text = getString(R.string.score, score)
        timerView.text = getString(R.string.timerValue, timeLeftInMillis / 1000)

        countDownTimer = initCountDownTimer(timeLeftInMillis)
        if (isGameStarted) {
            countDownTimer.start()
        }
    }

    private fun initCountDownTimer(timeToStartFrom: Long): CountDownTimer {
        return object : CountDownTimer(timeToStartFrom, countDownIntervalInMillis) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                timerView.text = getString(R.string.timerValue, timeLeftInMillis / 1000)
                timerView.startAnimation(animTime)
            }

            override fun onFinish() = endGame()
        }
    }

    private fun startGame() {
        countDownTimer.start()
        isGameStarted = true
    }

    private fun incrementScore() {
        score++
        scoreView.text = getString(R.string.score, score)
    }

    private fun resetGame() {
        score = 0
        scoreView.text = getString(R.string.score, score)
        timerView.text = getString(R.string.timerValue, initialStartTimeInMillis / 1000)

        countDownTimer = initCountDownTimer(initialStartTimeInMillis)
    }

    private fun endGame() {
        Toast.makeText(this, getString(R.string.endMessage, score), Toast.LENGTH_LONG).show()
        isGameStarted = false
        resetGame()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SCORE_KEY, score)
        outState.putLong(TIME_LEFT_KEY, timeLeftInMillis)
        outState.putBoolean(IS_GAME_STARTED, isGameStarted)
        countDownTimer.cancel()

        Log.d(TAG, "onSaveInstance called, saving Score $score and time left: $timeLeftInMillis")

        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuItemAbout) {
            showInfo()
        }
        return true
    }

    private fun showInfo() {
        AlertDialog.Builder(this)
            .setTitle(R.string.menuItemAboutValue)
            .setMessage(R.string.alertText)
            .create()
            .show()
    }
}