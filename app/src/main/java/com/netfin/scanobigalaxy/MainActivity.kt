package com.netfin.scanobigalaxy

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        setContentView(R.layout.activity_main)
        tvresult = findViewById<TextView>(R.id.txt_show)
        imresult = findViewById<ImageView>(R.id.imageView)
        button!!.setOnClickListener {
            val intent = Intent(this@MainActivity, ScanActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        var tvresult: TextView? = null
        var imresult: ImageView? = null
    }
}
