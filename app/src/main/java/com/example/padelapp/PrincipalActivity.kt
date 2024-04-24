package com.example.padelapp

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.net.InetAddress

class PrincipalActivity : AppCompatActivity() {

    //Variables
    private var ip: String? = intent.getStringExtra("IP")
    private lateinit var tvInfo: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initComponents()
        initListeners()
    }

    private fun initListeners() {
        TODO("Not yet implemented")
    }

    private fun initComponents() {
        tvInfo = findViewById(R.id.tv_info)
        tvInfo.text = "ip: ${ip}      Port: 8080"
    }
}