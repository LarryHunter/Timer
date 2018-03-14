package com.hunterdev.timerapp

import android.os.Bundle
import android.os.CountDownTimer
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.hunterdev.timerapp.Util.PrefUtil
import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.content_timer.*
import java.util.concurrent.TimeUnit

class TimerActivity : AppCompatActivity() {

    enum class TimerState {
        Stopped, Paused, Running
    }

    private lateinit var m_timer: CountDownTimer
    private var m_timerState: TimerState = TimerState.Stopped
    private var m_timerLengthSeconds: Long = 0
    private var m_secondsRemaining: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        setSupportActionBar(toolbar)
        supportActionBar?.setIcon(R.drawable.ic_timer_white)
        supportActionBar?.title = "  " + getString(R.string.app_name)

        fab_start.setOnClickListener { view ->
            startTimer()
            m_timerState = TimerState.Running
            updateButtons()

            Snackbar.make(view, "Timer Started", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show()
        }

        fab_pause.setOnClickListener { view ->
            m_timer.cancel()
            m_timerState = TimerState.Paused
            updateButtons()

            Snackbar.make(view, "Timer Paused", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        fab_stop.setOnClickListener { view ->
            m_timer.cancel()
            m_timerState = TimerState.Stopped
            onTimerFinished()

            Snackbar.make(view, "Timer Stopped", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show()
        }
    }

    override fun onResume() {
        super.onResume()

        intiTimer()

        // TODO: Remove background timer & hide notification
    }

    override fun onPause() {
        super.onPause()

        if (m_timerState == TimerState.Running) {
            m_timer.cancel()
            // TODO: Start background timer & show notification
        } else if (m_timerState == TimerState.Paused) {
            // TODO: Show notification
        }

        PrefUtil.setPreviousTimerLengthInSeconds(m_timerLengthSeconds, this)
        PrefUtil.setTimerLengthRemainingInSeconds(m_secondsRemaining, this)
        PrefUtil.setTimerState(m_timerState, this)
    }

    private fun intiTimer() {
        m_timerState = PrefUtil.getTimerState(this)

        if (m_timerState == TimerState.Stopped) {
            setNewTimerLength()
        } else {
            setPreviousTimerLength()
        }

        m_secondsRemaining = if (m_timerState == TimerState.Stopped || m_timerState == TimerState.Paused) {
            PrefUtil.getTimerLengthRemainingInSeconds(this)
        } else {
            m_timerLengthSeconds
        }

        // TODO: change m_secondsRemaining according to where the background timer left off

        // resume where we left off
        if (m_timerState == TimerState.Running) {
            startTimer()
        }

        updateButtons()
        updateCountdownUI()
    }

    private fun onTimerFinished() {
        m_timerState = TimerState.Stopped

        setNewTimerLength()
        id_countdown_progress.progress = 0
        PrefUtil.setTimerLengthRemainingInSeconds(m_timerLengthSeconds, this)
        m_secondsRemaining = m_timerLengthSeconds

        updateButtons()
        updateCountdownUI()
    }

    private fun startTimer() {
        m_timerState = TimerState.Running

        m_timer = object : CountDownTimer(TimeUnit.SECONDS.toMillis(m_secondsRemaining), TimeUnit.SECONDS.toMillis(1)) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                m_secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()
    }

    private fun setNewTimerLength() {
        val lengthInMinutes = PrefUtil.getTimerLengthInMinutes(this)
        m_timerLengthSeconds = TimeUnit.MINUTES.toSeconds(lengthInMinutes.toLong())
        id_countdown_progress.max = m_timerLengthSeconds.toInt()
    }

    private fun setPreviousTimerLength() {
        m_timerLengthSeconds = PrefUtil.getPreviousTimerLengthInSeconds(this)
        id_countdown_progress.max = m_timerLengthSeconds.toInt()
    }

    private fun updateCountdownUI() {
        val minutesUntilFinished = TimeUnit.SECONDS.toMinutes(m_secondsRemaining)
        val secondsInMinutesUntilFinished = m_secondsRemaining - TimeUnit.MINUTES.toSeconds(minutesUntilFinished)
        val secondsStr = secondsInMinutesUntilFinished.toString()
        id_countdown_text.text = "$minutesUntilFinished:${
        if (secondsStr.length == 2) secondsStr
        else "0" + secondsStr}"
        id_countdown_progress.progress = (m_timerLengthSeconds - m_secondsRemaining).toInt()
    }

    private fun updateButtons() {
        when (m_timerState) {
            TimerState.Running -> {
                fab_start.isEnabled = false
                fab_pause.isEnabled = true
                fab_stop.isEnabled = true
            }

            TimerState.Paused -> {
                fab_start.isEnabled = true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = true
            }

            TimerState.Stopped -> {
                fab_start.isEnabled = true
                fab_pause.isEnabled = false
                fab_stop.isEnabled = false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_timer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
