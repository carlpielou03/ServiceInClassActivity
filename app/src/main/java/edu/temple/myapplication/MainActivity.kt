package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import java.util.Timer
import kotlin.concurrent.timer

class MainActivity : AppCompatActivity() {
    lateinit var timerTextView: TextView
    lateinit var timerBinder: TimerService.TimerBinder
    var isConnected = false

    val startButton : Button by lazy {
        findViewById<Button>(R.id.startButton)
    }

    //  var curTime: Int = 0
    val timeHandler = Handler(Looper.getMainLooper()) {
        timerTextView.text = it.what.toString()
     //   curTime = it.what
        true
    }

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            timerBinder = service as TimerService.TimerBinder
            timerBinder.setHandler(timeHandler)
            isConnected = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isConnected = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerTextView = findViewById(R.id.textView)

        bindService(
            Intent(this, TimerService::class.java),
            serviceConnection,
            BIND_AUTO_CREATE
        )
        val startButton = findViewById<Button>(R.id.startButton)
        startButton.setOnClickListener {
            start()
        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {
           stop()
        }
    }

    private fun start() {
        if (isConnected) {
            if (timerBinder.isRunning) {
                timerBinder.pause()
                startButton.text = "UnPause"
            } else {

                if (timerBinder.paused) {
                    //   Log.d("TESTING PAUSE", curTime.toString())
                    //timerBinder.start(curTime)
                    timerBinder.pause()
                } else {
                    timerBinder.start(100)

                }
                startButton.text = "Pause"
            }
        }
    }

    private fun stop() {
        if (isConnected) {
            timerBinder.stop()
            if (timerBinder.paused) {
                // startButton.text = "Start"
            } else {
                startButton.text = "Start"
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

       return when(item.itemId) {
            R.id.action_play -> {start()
                true}

            R.id.action_stop -> {stop()
                true}

            else -> false
        }

       // return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        unbindService(serviceConnection)
        super.onDestroy()
    }
}