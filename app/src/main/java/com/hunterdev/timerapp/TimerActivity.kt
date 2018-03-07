package com.hunterdev.timerapp

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_timer.*

class TimerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        setSupportActionBar(toolbar)

        fab_start.setOnClickListener { view ->
            Snackbar.make(view, "Replace with START timer code", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show()
        }

        fab_pause.setOnClickListener { view ->
            Snackbar.make(view, "Replace with PAUSE timer code", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

            fab_stop.setOnClickListener { view ->
            Snackbar.make(view, "Replace with STOP timer code", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show()
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
