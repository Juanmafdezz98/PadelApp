package com.example.padelapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStreamWriter
import java.net.InetAddress
import java.net.Socket

class LoginActivity : AppCompatActivity() {

    private lateinit var ip: InetAddress
    private lateinit var etIP: EditText
    private lateinit var btConnect: Button
    private lateinit var tvConnect: TextView
    private lateinit var btSend : Button
    private lateinit var socket : Socket
    private val port: Int = 8080

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initComponents()
        initListeners()

    }

    private suspend fun connect(address: InetAddress) {
        withContext(Dispatchers.IO) {
            try {
                socket = Socket(address, port)
            } catch (e: Exception) {
                Log.i("Socket", "Exception")
                e.printStackTrace()
            }
        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun initListeners() {
        btConnect.setOnClickListener {
            ip = InetAddress.getByName(etIP.text.toString())
            CoroutineScope(IO).launch {
                connect(ip)
            }
        }

        btSend.setOnClickListener {
            if(::socket.isInitialized) {
                GlobalScope.launch(Dispatchers.IO) {
                    val outPutStream = OutputStreamWriter(socket.getOutputStream())
                    outPutStream.write("HELLO SERVER")
                    outPutStream.flush()

                    launch(Dispatchers.Main){
                        tvConnect.setText(R.string.received)
                    }
                }
            }
        }

    }

    private fun initComponents() {
        btConnect = findViewById(R.id.btn_connect)
        etIP = findViewById(R.id.ip_add)
        tvConnect = findViewById(R.id.tv_connect)
        btSend = findViewById(R.id.btSend)
    }
}