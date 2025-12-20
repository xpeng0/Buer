package com.cscyxp.buer.utils

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Icon
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.cscyxp.buer.MyApp
import com.cscyxp.buer.R

private const val TAG = "NotificationUtil"
object NotificationUtil {
    const val CHANNEL_PICKUP_CODE = "pickup_code_channel";
    fun createNotificationChannels() {
        val channel = NotificationChannel(
            CHANNEL_PICKUP_CODE,
            "取餐码通知",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "自动提取并展示外卖平台取餐码"
            enableLights(true)
            lightColor = Color.BLUE
        }
        val manager = MyApp.appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }


    fun notify(id: Int, contextText: String) {
        val appContext = MyApp.appContext
        val manager = NotificationManagerCompat.from(appContext)
        val notification = NotificationCompat.Builder(appContext, CHANNEL_PICKUP_CODE)
            .setSmallIcon(R.drawable.ic_game)
            .setContentTitle("取餐码汇总")
            .setContentText(contextText)
            .setAutoCancel(false)
            .build()
        if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            manager.notify(id, notification)
        } else {
            Log.i(TAG, "notify: 无通知权限")
        }
    }

}