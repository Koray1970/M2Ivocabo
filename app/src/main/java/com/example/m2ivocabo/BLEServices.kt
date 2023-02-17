package com.example.m2ivocabo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.graphics.BitmapFactory
import android.os.Build
import android.provider.Settings.Global.getString
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.m2ivocabo.R.mipmap.bluetooth_disconnected
import java.util.*


class BLEServices(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager

    override suspend fun doWork(): Result {
        val progress = "Starting Download"
        setForeground(createForegroundInfo(progress))
        Log.v(LTAG, "Notification is INIT!!!")
        return Result.success()
    }

    private fun createForegroundInfo(progress: String): ForegroundInfo {

        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(UUID.randomUUID())
        // Create a Notification channel if necessary
        createChannel()
        val notification = NotificationCompat.Builder(applicationContext, "idNotification")
            .setContentTitle("title")
            .setTicker("title")
            .setContentText(progress)
            .setSmallIcon(R.drawable.baseline_bluetooth_connected_white_24)
            .setOngoing(true)
            .setChannelId(CHANNEL_ID)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setLargeIcon(BitmapFactory.decodeResource(applicationContext.resources, bluetooth_disconnected) )
            //.setVibrate(listOf(1L, 2L, 3L).toLongArray())
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(
                android.R.drawable.ic_delete,
                applicationContext.getString(R.string.btncancel),
                intent
            )
            .build()
        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    private fun createChannel() {
        // Create a Notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "Channel Name"//getString(R.string.channel_name)
            val descriptionText = "Channel Description"//getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH// .IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                description = descriptionText
            }
            //mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    companion object {
        private val LTAG = BLEServices::class.java.simpleName
        var NOTIFICATION_ID = 150//Random(10000).nextInt()
        const val ARG_PROGRESS = "Progress"
        const val CHANNEL_ID = "Job progress"
        const val TAG = "ForegroundWorker"
    }

}