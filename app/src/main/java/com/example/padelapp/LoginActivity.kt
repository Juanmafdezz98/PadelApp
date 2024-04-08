package com.example.padelapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    private lateinit var fabNext: FloatingActionButton
    private lateinit var btSend: Button
    private lateinit var socket: Socket
    private val context = this@LoginActivity
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
        withContext(IO) {
            try {
                socket = Socket(address, port)
            } catch (e: Exception) {
                Log.i("Socket", "Exception")
                /*withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "Unable to establish connection with the provided IP address. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }*/
                context.runOnUiThread {
                    Toast.makeText(
                        context,
                        "Unable to establish connection with the provided IP address. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
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
            if (::socket.isInitialized) {
                GlobalScope.launch(IO) {
                    val outPutStream = OutputStreamWriter(socket.getOutputStream())
                    outPutStream.write("HELLO SERVER")
                    outPutStream.flush()

                    launch(Dispatchers.Main) {
                        tvConnect.setText(R.string.received)
                    }
                }
            }
            else noConnected()
        }

        fabNext.setOnClickListener {
            val intent = Intent(this, PrincipalActivity::class.java)
            if (::socket.isInitialized) startActivity(intent)
            noConnected()
        }
    }

    private fun initComponents() {
        btConnect = findViewById(R.id.btn_connect)
        etIP = findViewById(R.id.ip_add)
        tvConnect = findViewById(R.id.tv_connect)
        btSend = findViewById(R.id.btSend)
        fabNext = findViewById(R.id.btnForward)
    }

    private fun noConnected() {
        Toast.makeText(
            this,
            "Connection to the server has not been established. Please connect first",
            Toast.LENGTH_SHORT
        ).show()
    }

}