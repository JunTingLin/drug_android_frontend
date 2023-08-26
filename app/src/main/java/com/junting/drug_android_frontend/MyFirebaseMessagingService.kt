package com.junting.drug_android_frontend

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.RemoteMessage

const val channelId = "notification_channel"
const val channelName = "com.junting.drug_android_frontend"
class MyFirebaseMessagingService : FirebaseMessagingService(){

    override fun onNewToken(token: String) {
        Log.d("New token", "$token")
    }
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

//        if (remoteMessage.data.isNotEmpty()) {
        Log.d("Message", remoteMessage.notification!!.title!!)
        generateNotification(remoteMessage.notification!!.title!!, remoteMessage.notification!!.body!!)
//        }
    }

//    @SuppressLint("RemoteViewLayout")
//    fun getRemoteView(notificationTitle: String, notificationDescription: String) : RemoteViews{
//
//        val remoteViews = RemoteViews("com.junting.drug_android_frontend", R.layout.notification)
//
//        remoteViews.setTextViewText(R.id.notification_title, notificationTitle)
//        remoteViews.setTextViewText(R.id.notification_description, notificationDescription)
//        remoteViews.setImageViewResource(R.id.notification_logo, R.drawable.app_image)
//
//        return remoteViews
//    }
    fun generateNotification(notificationTitle: String, notificationDescription: String){

        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.app_image)
            .setContentTitle(notificationTitle)
            .setContentText(notificationDescription)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)


//        builder = builder.setContent(getRemoteView(notificationTitle, notificationDescription))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(0, builder.build())
    }
}
