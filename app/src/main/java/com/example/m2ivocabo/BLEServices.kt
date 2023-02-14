package com.example.m2ivocabo

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

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager



    override suspend fun doWork(): Result {
        val progress = "Starting Download"
        setForeground(createForegroundInfo(progress))
        return Result.success()
    }

    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val id = applicationContext.getString(R.string.notification_channel_id)
        val title = applicationContext.getString(R.string.notification_title)
        val cancel = applicationContext.getString(R.string.btncancel)
        // This PendingIntent can be used to cancel the worker
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(getId())

        // Create a Notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }

        val notification = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(progress)
            .setSmallIcon(R.drawable.baseline_bluetooth_connected_white_24)
            .setOngoing(true)
            // Add the cancel action to the notification which can
            // be used to cancel the worker
            .addAction(android.R.drawable.ic_delete, cancel, intent)
            .build()
        val notificationId= Random(10000)
        return ForegroundInfo(notificationId.nextInt(), notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        // Create a Notification channel
    }

}