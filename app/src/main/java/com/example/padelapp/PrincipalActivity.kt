package com.example.padelapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.slider.RangeSlider

class PrincipalActivity : AppCompatActivity() {

    //Variables
    private var speed: Int = 40
    private var balls: Int = 5
    private var spin: Int = 2
    private var elev: Int = 5
    private var feed: Int = 5
    private val context = this@PrincipalActivity
    private var ip = "192.168.1.131"


    //Variables xml
    //private var ip: String? = intent.getStringExtra("IP")
    private lateinit var tvInfo: TextView
    private lateinit var btLobs: Button
    private lateinit var btDrops: Button
    private lateinit var btWalls: Button
    private lateinit var btSmash: Button
    private lateinit var btInfo: ImageButton
    private lateinit var tvSpeed: TextView
    private lateinit var tvBalls: TextView
    private lateinit var tvSpin: TextView
    private lateinit var tvHeight: TextView
    private lateinit var tvFeed: TextView
    private lateinit var fabAddBall: FloatingActionButton
    private lateinit var fabMinusBall: FloatingActionButton
    private lateinit var fabAddSpin: FloatingActionButton
    private lateinit var fabMinusSpin: FloatingActionButton
    private lateinit var fabAddFeed: FloatingActionButton
    private lateinit var fabMinusFeed: FloatingActionButton
    private lateinit var rsSpeed: RangeSlider
    private lateinit var rsHeight: RangeSlider
    private lateinit var tvSpeedP: TextView
    private lateinit var tvBallsP: TextView
    private lateinit var tvSpinP: TextView
    private lateinit var tvHeightP: TextView
    private lateinit var tvFeedP: TextView
    private lateinit var btSend: Button
    private lateinit var btReset: Button


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
        initUI()
    }

    private fun initListeners() {
        btInfo.setOnClickListener {
            val intent = Intent(this,InfoActivity::class.java)
            startActivity(intent)
        }
        rsSpeed.addOnChangeListener { _, value, _ ->
            speed = value.toInt()
            setSpeed()
        }
        rsHeight.addOnChangeListener { _, value, _ ->
            elev = value.toInt()
            setHeight()
        }
        fabAddBall.setOnClickListener {
            if (balls == 20) {
                context.runOnUiThread {
                    Toast.makeText(
                        context,
                        "You have reached the maximum. The robot cannot throw more than 20 balls",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                balls += 1
                setBalls()
            }
        }
        fabMinusBall.setOnClickListener {
            if (balls == 1) {
                context.runOnUiThread {
                    Toast.makeText(
                        context,
                        "You have reached the minimum. The robot cannot throw fewer than 1 ball",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                balls -= 1
                setBalls()
            }

        }
        fabAddFeed.setOnClickListener {
            if (feed == 15) {
                context.runOnUiThread {
                    Toast.makeText(
                        context,
                        "The robot cannot throw balls with greater interval than a ball per 15 seconds",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                feed += 1
                setFeed()
            }
        }
        fabMinusFeed.setOnClickListener {
            if (feed == 1) {
                context.runOnUiThread {
                    Toast.makeText(
                        context,
                        "The robot cannot throw fewer than 1 ball per second",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                feed -= 1
                setFeed()
            }
        }
        fabAddSpin.setOnClickListener {
            if (spin == 10) {
                context.runOnUiThread {
                    Toast.makeText(
                        context,
                        "You have reached the maximum of ball spin",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                spin += 1
                setSpin()
            }
        }
        fabMinusSpin.setOnClickListener {
            if (spin == 0) {
                context.runOnUiThread {
                    Toast.makeText(
                        context,
                        "You have reached the minimum of ball spin. This is straight throw",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                spin -= 1
                setSpin()
            }
        }
        btSend.setOnClickListener {

        }


    }

    private fun initComponents() {
        tvInfo = findViewById(R.id.tvInfo)
        tvInfo.text = "ip: ${ip}      Port: 8080"
        btLobs = findViewById(R.id.btnLobs)
        btDrops = findViewById(R.id.btnDrops)
        btWalls = findViewById(R.id.btnWall)
        btSmash = findViewById(R.id.btnSmash)
        btInfo = findViewById(R.id.btnInfo)
        tvSpeed = findViewById(R.id.tvSpeed)
        tvBalls = findViewById(R.id.tvBalls)
        tvSpin = findViewById(R.id.tvSpin)
        tvHeight = findViewById(R.id.tvHeight)
        tvFeed = findViewById(R.id.tvFeed)
        tvSpeedP = findViewById(R.id.tvSpeedP)
        tvBallsP = findViewById(R.id.tvBallsP)
        tvSpinP = findViewById(R.id.tvSpinP)
        tvHeightP = findViewById(R.id.tvHeightP)
        tvFeedP = findViewById(R.id.tvFeedP)
        fabAddBall = findViewById(R.id.fabAddBall)
        fabMinusBall = findViewById(R.id.fabMinusBall)
        fabAddSpin = findViewById(R.id.fabAddSpin)
        fabMinusSpin = findViewById(R.id.fabMinusSpin)
        fabAddFeed = findViewById(R.id.fabAddFeed)
        fabMinusFeed = findViewById(R.id.fabMinusFeed)
        rsSpeed = findViewById(R.id.rsSpeed)
        rsHeight = findViewById(R.id.rsHeight)
        btSend = findViewById(R.id.btSend)
        btReset = findViewById(R.id.btReset)
    }

    private fun initUI() {
        setSpeed()
        setBalls()
        setSpin()
        setHeight()
        setFeed()
    }

    private fun setFeed() {
        tvFeed.text = feed.toString()
        tvFeedP.text = feed.toString()
    }

    private fun setHeight() {
        tvHeight.text = elev.toString()
        tvHeightP.text = elev.toString()
    }

    private fun setSpin() {
        tvSpin.text = spin.toString()
        tvSpinP.text = spin.toString()
    }

    private fun setBalls() {
        tvBalls.text = balls.toString()
        tvBallsP.text = balls.toString()
    }

    private fun setSpeed() {
        tvSpeed.text = speed.toString()
        tvSpeedP.text = speed.toString()
    }

}