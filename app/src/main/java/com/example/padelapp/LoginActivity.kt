package com.example.padelapp

import android.content.Intent
import android.os.Bundle
import android.os.NetworkOnMainThreadException
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.InetAddress
import java.net.Socket
import java.net.SocketException

class LoginActivity : AppCompatActivity() {

    //Variables
    private lateinit var ip: InetAddress
    private lateinit var etIP: EditText
    private lateinit var etPort: EditText
    private lateinit var btConnect: Button
    private lateinit var tvConnect: TextView
    private lateinit var fabNext: FloatingActionButton
    private lateinit var btSend: Button
    private lateinit var socket: Socket
    private lateinit var check: ImageView
    private val context = this@LoginActivity

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

    private fun initComponents() {
        btConnect = findViewById(R.id.btn_connect)
        etIP = findViewById(R.id.ip_add)
        etPort = findViewById(R.id.etPort)
        tvConnect = findViewById(R.id.tv_connect)
        btSend = findViewById(R.id.btSend)
        fabNext = findViewById(R.id.btnForward)
        check = findViewById(R.id.checked)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun initListeners() {
        btConnect.setOnClickListener {
            try {
                ip = InetAddress.getByName(etIP.text.toString())
                val port = etPort.text.toString().toInt()
                CoroutineScope(IO).launch {
                    connect(ip, port)
                }
            } catch (e: NetworkOnMainThreadException) {
                Log.i("Socket", "Exception")
                context.runOnUiThread {
                    Toast.makeText(
                        context,
                        "IP Address no valid. Please, enter a valid IP address and try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: NumberFormatException){
                Log.i("Socket", "Exception")
                context.runOnUiThread {
                    Toast.makeText(
                        context,
                        "Fields are empty. Please enter an IP address and try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        btSend.setOnClickListener {
            if (::socket.isInitialized) {
                GlobalScope.launch(IO) {
                    val outPutStream = OutputStreamWriter(socket.getOutputStream())
                    outPutStream.write("HELLO SERVER")
                    outPutStream.flush()

                    /*launch(Dispatchers.Main) {
                        tvConnect.setText(R.string.received)
                    }*/
                }
            } else noConnected()
        }

        fabNext.setOnClickListener {
            val intent = Intent(this, PrincipalActivity::class.java)
            intent.putExtra("IP", etIP.text.toString())
            intent.putExtra("PORT", etPort.text.toString())
            if (::socket.isInitialized) {
                startActivity(intent)
            }else{
                noConnected()
            }
        }

        etPort.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                s?.let {
                    val filteredText = it.filter { char -> char.isDigit() }
                    if (filteredText.length != it.length) {
                        context.runOnUiThread {
                            Toast.makeText(
                                context,
                                "Ports can only contain numbers",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        etPort.setText(filteredText)
                        etPort.setSelection(filteredText.length)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        etIP.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {

                    val filteredText = it.filter { char -> char.isDigit() || char == '.' } //Just numbers and dots

                    //Max. 3 dots and no spaces allowed
                    val parts = filteredText.split('.')
                    val isValid = parts.size <= 4 && parts.all { part -> part.isEmpty() || part.toIntOrNull() in 0..255 }

                    if (!isValid) {
                        context.runOnUiThread {
                            Toast.makeText(
                                context,
                                "Invalid IP introduced. Follow this format: X.X.X.X where X [0, 255]",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        val validText = it.substring(0, start)
                        etIP.setText(validText)
                        etIP.setSelection(validText.length)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

    }

    private fun noConnected() {
        Toast.makeText(
            this,
            "Connection to the server has not been established. Please connect",
            Toast.LENGTH_SHORT
        ).show()
    }

    private suspend fun connect(address: InetAddress, port: Int) {
        try {
            socket = withContext(IO) {
                Socket(address, port)
            }
            if (::socket.isInitialized) {
                context.runOnUiThread {
                    Toast.makeText(
                        context,
                        "Connection with server was established correctly",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                runBlocking {
                    launch {
                        readMessages(socket)
                    }
                }
            } else {
                context.runOnUiThread {
                    Toast.makeText(
                        context,
                        "Server is not available at this moment. Please, try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: TimeoutCancellationException) {
            context.runOnUiThread {
                Toast.makeText(
                    context,
                    "Connection timeout. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: SocketException) {
            context.runOnUiThread {
                Toast.makeText(
                    context,
                    "Server was disconnected and communication lost. Please check",
                    Toast.LENGTH_SHORT
                ).show()
            }
            backToConnect()
            e.printStackTrace()
        }finally {
            if (!::socket.isInitialized) {
                withContext(IO) {
                    socket.close()
                }
            }
        }
    }
    private fun backToConnect() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    private suspend fun readMessages(socket: Socket) {
        withContext(IO) {
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

            while (true) {
                val message = reader.readLine()
                if (message == "RECIBIDO") {
                    launch(Dispatchers.Main) {
                        tvConnect.setText(R.string.received)
                        check.visibility = View.VISIBLE
                    }
                }else{
                    launch(Dispatchers.Main) {
                        tvConnect.setText(R.string.noreceived)
                    }
                }
            }
        }

    }
}