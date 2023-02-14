package com.example.m2ivocabo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlin.random.Random


class BLEServices(context: Context, parameters: WorkerParameters):
    CoroutineWorker(context, parameters) {

    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
        .setSmallIcon(R.drawable.baseline_bluetooth_connected_white_24)
        .setContentTitle("Important background job")

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        createChannel()
        val notification = notificationBuilder.build()
        val foregroundInfo = ForegroundInfo(NOTIFICATION_ID, notification)

        setForeground(foregroundInfo)
        createForegroundInfo()


        return Result.success()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun createForegroundInfo() {
        val id = applicationContext.getString(R.string.notification_channel_id)
        val title = applicationContext.getString(R.string.notification_title)
        val cancel = applicationContext.getString(R.string.btncancel)
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(getId())

        createChannel()


        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText("progress")
            .setSmallIcon(R.drawable.baseline_bluetooth_connected_white_24)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()
        //val notificationId=150// Random(10000)
        val foregroundInfo = ForegroundInfo(NOTIFICATION_ID, notification)
        setForeground(foregroundInfo)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = notificationManager?.getNotificationChannel(CHANNEL_ID)
            if (notificationChannel == null) {
                notificationManager?.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID, TAG, NotificationManager.IMPORTANCE_LOW
                    )
                )
            }
        }
    }
    companion object{
        var NOTIFICATION_ID=150//Random(10000).nextInt()
        const val ARG_PROGRESS = "Progress"
        const val CHANNEL_ID = "Job progress"
        const val TAG = "ForegroundWorker"
    }

}