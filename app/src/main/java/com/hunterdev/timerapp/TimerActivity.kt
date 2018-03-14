package com.hunterdev.timerapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.hunterdev.timerapp.utils.NotificationUtil
import com.hunterdev.timerapp.utils.PrefUtil
import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.content_timer.*
import java.util.*
import java.util.concurrent.TimeUnit

class TimerActivity : AppCompatActivity() {

    companion object {
        private const val ALARM_WAKE_REQUEST_CODE: Int = 0

        fun setAlarm(context: Context, nowInSeconds: Long, secondsRemaining: Long): Long {
            val wakeUpTime = TimeUnit.SECONDS.toMillis(nowInSeconds + secondsRemaining)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, ALARM_WAKE_REQUEST_CODE, intent, 0)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
            PrefUtil.setAlarmSetTime(nowInSeconds, context)
            return wakeUpTime
        }

        fun removeAlarm(context: Context) {
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, ALARM_WAKE_REQUEST_CODE, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            PrefUtil.setAlarmSetTime(0, context)
        }

        val nowInSeconds: Long
                get() = TimeUnit.MILLISECONDS.toSeconds(Calendar.getInstance().timeInMillis)
    }

    enum class TimerState {
        Stopped, Paused, Running
    }

    private lateinit var timer: CountDownTimer
    private var timerState: TimerState = TimerState.Stopped
    private var timerLengthInSeconds: Long = 0
    private var secondsRemaining: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        setSupportActionBar(toolbar)
        supportActionBar?.setIcon(R.drawable.ic_timer_white)
        supportActionBar?.title = "  " + getString(R.string.app_name)

        fab_start.setOnClickListener { view ->
            startTimer()
            timerState = TimerState.Running
            updateButtons()

            Snackbar.make(view, "Running", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show()
        }

        fab_pause.setOnClickListener { view ->
            timer.cancel()
            timerState = TimerState.Paused
            updateButtons()

            Snackbar.make(view, "Paused", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        fab_stop.setOnClickListener { view ->
            timer.cancel()
            val snackMsg = if (timerState == TimerState.Running) {
                "Stopped"
            } else {
                "Reset"
            }
            timerState = TimerState.Stopped
            onTimerFinished()

            Snackbar.make(view, snackMsg, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show()
        }
    }

    override fun onResume() {
        super.onResume()

        intiTimer()
        removeAlarm(this)
        NotificationUtil.hideTimerNotification(this)
    }

    override fun onPause() {
        super.onPause()

        if (timerState == TimerState.Running) {
            timer.cancel()
            val wakeUpTime = setAlarm(this, nowInSeconds, secondsRemaining)
            NotificationUtil.showTimerRunning(this, wakeUpTime)
        } else if (timerState == TimerState.Paused) {
            NotificationUtil.showTimerPaused(this)
        }

        PrefUtil.setPreviousTimerLengthInSeconds(timerLengthInSeconds, this)
        PrefUtil.setTimerLengthRemainingInSeconds(secondsRemaining, this)
        PrefUtil.setTimerState(timerState, this)

        /*
        var secondsRemaining = PrefUtil.getTimerLengthRemainingInSeconds(context)
        val alarmSetTime = PrefUtil.getAlarmSetTime(context)
        val nowSeconds = TimerActivity.nowInSeconds

        TimerActivity.removeAlarm(context)
        secondsRemaining -= (nowSeconds - alarmSetTime)
        PrefUtil.setTimerLengthRemainingInSeconds(secondsRemaining, context)
        PrefUtil.setTimerState(TimerActivity.TimerState.Paused, context)
        NotificationUtil.showTimerPaused(context)
        */
    }

    private fun intiTimer() {
        timerState = PrefUtil.getTimerState(this)

        if (timerState == TimerState.Stopped) {
            setNewTimerLength()
        } else {
            setPreviousTimerLength()
        }

        secondsRemaining = if (timerState == TimerState.Stopped || timerState == TimerState.Paused) {
            PrefUtil.getTimerLengthRemainingInSeconds(this)
        } else {
            timerLengthInSeconds
        }

        val alarmSetTime = PrefUtil.getAlarmSetTime(this)
        if (alarmSetTime > 0) {
            secondsRemaining -= (nowInSeconds - alarmSetTime)
        }

        if (secondsRemaining <= 0) {
            onTimerFinished()
        } else if (timerState == TimerState.Running) {
            startTimer()
        }

        updateButtons()
        updateCountdownUI()
    }

    private fun onTimerFinished() {
        timerState = TimerState.Stopped

        setNewTimerLength()
        id_countdown_progress.progress = 0
        PrefUtil.setTimerLengthRemainingInSeconds(timerLengthInSeconds, this)
        secondsRemaining = timerLengthInSeconds

        updateButtons()
        updateCountdownUI()
    }

    private fun startTimer() {
        timerState = TimerState.Running

        timer = object : CountDownTimer(TimeUnit.SECONDS.toMillis(secondsRemaining), TimeUnit.SECONDS.toMillis(1)) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)
                updateCountdownUI()
            }
        }.start()
    }

    private fun setNewTimerLength() {
        val lengthInMinutes = PrefUtil.getTimerLengthInMinutes(this)
        timerLengthInSeconds = TimeUnit.MINUTES.toSeconds(lengthInMinutes.toLong())
        id_countdown_progress.max = timerLengthInSeconds.toInt()
    }

    private fun setPreviousTimerLength() {
        timerLengthInSeconds = PrefUtil.getPreviousTimerLengthInSeconds(this)
        id_countdown_progress.max = timerLengthInSeconds.toInt()
    }

    private fun updateCountdownUI() {
        val minutesUntilFinished = TimeUnit.SECONDS.toMinutes(secondsRemaining)
        val secondsInMinutesUntilFinished = secondsRemaining - TimeUnit.MINUTES.toSeconds(minutesUntilFinished)
        val secondsStr = secondsInMinutesUntilFinished.toString()
        @Suppress("ConvertToStringTemplate")
        val countdownText = "$minutesUntilFinished:${
        if (secondsStr.length == 2) secondsStr
        else "0" + secondsStr}"
        id_countdown_text.text = countdownText
        id_countdown_progress.progress = (timerLengthInSeconds - secondsRemaining).toInt()
    }

    private fun updateButtons() {
        when (timerState) {
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
            R.id.action_settings -> {
                val settingsIntent = Intent(this, SettingsActivity::class.java)
                startActivity(settingsIntent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
