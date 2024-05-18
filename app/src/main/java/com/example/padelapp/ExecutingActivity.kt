package com.example.padelapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.InetAddress
import java.net.Socket

class ExecutingActivity : AppCompatActivity() {

    private lateinit var socket: Socket
    private lateinit var tvTime: TextView
    private lateinit var btPause: Button
    private lateinit var btPlay: Button
    private lateinit var btTerminate: Button
    private lateinit var ip: String
    private lateinit var port: String
    private var balls: Int = 0
    private var feed: Int = 0

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
        btPlay.setOnClickListener {

        }

        btPause.setOnClickListener {

        }

        btTerminate.setOnClickListener {
            try {
                CoroutineScope(Dispatchers.IO).launch {
                    connectSocket(InetAddress.getByName(ip), port.toInt())
                }
            } catch (e: Exception) {
                Log.i("Socket", "Exception")
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private suspend fun connectSocket(address: InetAddress, port: Int) {
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
        feed = intent.extras?.getInt("FEED")!!
        tvTime = findViewById(R.id.tvTime)
        btPlay = findViewById(R.id.btPlay)
        btPause = findViewById(R.id.btPause)
        btTerminate = findViewById(R.id.btTerminate)
    }
}