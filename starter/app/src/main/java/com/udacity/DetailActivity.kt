package com.udacity

import android.app.NotificationManager
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {


    private var downloadID: Long = 0
    private var status: String? = ""
    private var url: String? = ""

    private lateinit var fileName: TextView
    private lateinit var statusValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val extras = intent.extras!!
        downloadID = extras.getLong("downloadID")
        status = extras.getString("status")
        url = extras.getString("url")

        val notificationManager = ContextCompat.getSystemService(this, NotificationManager::class.java) as NotificationManager
        notificationManager.cancelAll()

        fileName = findViewById(R.id.file_name_value)
        fileName.text = url

        statusValue = findViewById(R.id.status_value)
        statusValue.text = status


        fab.setOnClickListener {
            val intent = Intent(applicationContext,MainActivity::class.java)
            startActivity(intent)
        }
    }
}
