package com.matchora.app.service

import android.app.PendingIntent
import android.context.Intent
import androidx.core.app.NotificationCompat
mmport androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.matchora.app.MainActivity
import com.matchora.app.MatchoraApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchoraMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Token saved to Firestore via Session user ID
    }

    override fun onMessageReceived(msg: RemoteMessage) {
        super.onMessageReceived(msg)
        val data = msg.data
        val type = data["type"] ?: return
        val title = data["title"] ?: "Matchora"
        val body = data["body"] ?: ""

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pi = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = when (type) {
            "message" -> MatchoraApp.CHANNEL_MESSAGES
            "match", "like" -> MatchoraApp.CHANNEL_MATCHES
            else -> MatchoraApp.CHANNEL_GENERAL
        }

        val notif = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_email)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pi)
            .build()

        NotificationManagerCompat.from(this).notify(title.hashCode(), notif)
    }
}
