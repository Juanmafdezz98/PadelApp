package com.example.padelapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.InetAddress
import java.net.Socket
import java.text.DecimalFormat
import kotlin.math.roundToInt

class ExecutingActivity : AppCompatActivity() {

    private lateinit var socket: Socket
    private lateinit var tvTime: TextView
    private lateinit var tvBallsCount: TextView
    private lateinit var btTerminate: Button
    private lateinit var ip: String
    private lateinit var port: String
    private var balls: Int = 0
    private var totalBalls: Int = 0
    private var ballCount: Int = 1
    private lateinit var progressBar: ProgressBar

    private lateinit var countDownTimer: CountDown
    private var interval: Int = 0
    private var secondsLeft = 0
    private var clockTime: Long = 0L
    private var progressTime: Float = 0.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_executing)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initComponents()
        initListeners()

    }

    private fun initListeners() {

        btTerminate.setOnClickListener {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    terminateExecution(InetAddress.getByName(ip), port.toInt())
                }
            } catch (e: Exception) {
                Log.i("Socket", "Exception")
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun terminateExecution(address: InetAddress, port: Int) {
        try {
            socket = withContext(Dispatchers.IO) {
                Socket(address, port)
            }
            if (::socket.isInitialized) {
                GlobalScope.launch(Dispatchers.IO) {
                    val outPutStream = OutputStreamWriter(socket.getOutputStream())
                    outPutStream.write("STOP")
                    outPutStream.flush()
                }
            }
            finish()
        } catch (e: Exception) {
            Log.i("Socket", "Exception")
        }
    }

    private fun initComponents() {
        ip = intent.extras?.getString("IP").orEmpty()
        port = intent.extras?.getString("PORT").orEmpty()
        balls = intent.extras?.getInt("BALLS")!!
        totalBalls = balls
        interval = intent.extras?.getInt("FEED")!!
        clockTime = (interval * 1000).toLong()
        progressTime = (clockTime / 1000).toFloat()
        tvTime = findViewById(R.id.tvTime)
        btTerminate = findViewById(R.id.btTerminate)
        progressBar = findViewById(R.id.progressBar)
        tvBallsCount = findViewById(R.id.tvBallsCount)
        countDownTimer = CountDown(clockTime, 1000)
        setBallsCount()
        countDownTimer.onTick = { millisUntilFinished ->
            val second = (millisUntilFinished / 1000.0f).roundToInt()
            if (second != secondsLeft) {
                secondsLeft = second

                timerFormat(
                    secondsLeft,
                    tvTime
                )
            }
        }
        countDownTimer.onFinish = {
            timerFormat(
                0,
                tvTime
            )
            balls --
            if (balls == 1) {
                CoroutineScope(Dispatchers.Main).launch {
                    delay(1500)
                    countDownTimer.destroyTimer()
                    finish()
                }
            }else{
                progressBar.progress = progressTime.toInt()
                countDownTimer.restartTimer()
            }
            ballCount++
            setBallsCount()
        }
        progressBar.max = progressTime.toInt()
        progressBar.progress = progressTime.toInt()
        countDownTimer.startTimer()

    }

    @SuppressLint("SetTextI18n")
    private fun setBallsCount() {
        tvBallsCount.text = "$ballCount/$totalBalls balls"
    }

    private fun timerFormat(secondsLeft: Int, tvTime: TextView) {
        progressBar.progress = secondsLeft
        val decimalFormat = DecimalFormat("00")
        val seconds = decimalFormat.format(secondsLeft)
        tvTime.text = seconds
    }

}