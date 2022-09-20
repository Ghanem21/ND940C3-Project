package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    lateinit var downloadManager: DownloadManager

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
        custom_button.setOnClickListener {
            setUrlFromRadio()
            download()
        }

        createChannel(
            getString(R.string.details_channel_id),
            getString(R.string.details_channel_name)
        )
    }

    private fun setUrlFromRadio() {
        URL = when {
            glide_radio.isChecked -> {
                getString(R.string.glide_url)
            }
            project3_radio.isChecked -> {
                getString(R.string.project3_url)
            }
            retrofit_radio.isChecked -> {
                getString(R.string.retrofit_url)
            }
            else -> {
                ""
            }
        }
        custom_button.setState(ButtonState.Clicked)
    }


    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (downloadID == id) {

                custom_button.setState(ButtonState.Completed)
                val query = DownloadManager.Query().setFilterById(id)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val status =
                        when (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                            DownloadManager.STATUS_SUCCESSFUL -> "Success"
                            else -> "Failed"
                        }

                    sendNotification(context, id, status)
                    cursor.close()
                }
            }
        }
    }

    private fun sendNotification(context: Context?, id: Long, status: String) {
        notificationManager.sendNotification(
            context?.getText(R.string.file_ready).toString(),
            applicationContext,
            id,
            status,
            URL
        )
    }

    private fun download() {

        custom_button.setState(ButtonState.Loading)
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)
                .setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, FILE_NAME)
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID = downloadManager.enqueue(request)

    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)


            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private var URL: String = ""
        private const val FILE_NAME = "LoadAppDownloaded"
    }

}