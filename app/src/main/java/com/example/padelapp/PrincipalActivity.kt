package com.example.padelapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
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
    private lateinit var btLobs: Button
    private lateinit var btDrops: Button
    private lateinit var btWalls: Button
    private lateinit var btSmash: Button
    private lateinit var btInfo: Button



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
        btInfo.setOnClickListener {
            val intent = Intent(this,ShotsInfoActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initComponents() {
        tvInfo = findViewById(R.id.tv_info)
        tvInfo.text = "ip: ${ip}      Port: 8080"
        btLobs = findViewById(R.id.btnLobs)
        btDrops = findViewById(R.id.btnDrops)
        btWalls = findViewById(R.id.btnWall)
        btSmash = findViewById(R.id.btnSmash)
        btInfo = findViewById(R.id.btnInfo)

    }
}