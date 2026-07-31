package com.example.data.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class SchoolFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "FCM Push Message received from: ${remoteMessage.from}")

        val title = remoteMessage.notification?.title
            ?: remoteMessage.data["title"]
            ?: "School Notice - Gayatri Bal Vidhya Niketan"

        val body = remoteMessage.notification?.body
            ?: remoteMessage.data["body"]
            ?: "A new school-wide notice has been published in the parent portal."

        val isUrgent = remoteMessage.data["isUrgent"]?.toBoolean() ?: false

        showNotification(applicationContext, title, body, isUrgent)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed FCM Device Token: $token")
        FcmNoticeManager.saveFcmToken(applicationContext, token)
    }

    companion object {
        private const val TAG = "SchoolFCMService"
        const val CHANNEL_ID = "school_notices_channel"
        const val CHANNEL_NAME = "School Notices & Announcements"

        fun showNotification(context: Context, title: String, body: String, isUrgent: Boolean = false) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    if (isUrgent) NotificationManager.IMPORTANCE_HIGH else NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Notifications for new school notices, fee reminders, and parent alerts."
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }

            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(if (isUrgent) "🚨 [URGENT] $title" else "📢 $title")
                .setContentText(body)
                .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(if (isUrgent) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)

            val notificationId = (System.currentTimeMillis() % 10000).toInt()
            notificationManager.notify(notificationId, builder.build())
        }
    }
}
