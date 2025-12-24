package com.cscyxp.buer.utils

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Icon
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.cscyxp.buer.MainActivity
import com.cscyxp.buer.MyApp
import com.cscyxp.buer.R

private const val TAG = "NotificationUtil"
object NotificationUtil {
    const val CHANNEL_PICKUP_CODE = "pickup_code_channel"
    const val CHANNEL_BASE = "base_channel"
    const val BASE_NOTIFICATION_ID = 1001

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
        val foregroundChannel = NotificationChannel(
            CHANNEL_BASE,
            "便捷记账常驻通知",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "展示当月消费，并支持点击跳转记账"
            enableLights(true)
            lightColor = Color.BLUE
        }
        val manager = MyApp.appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
        manager.createNotificationChannel(foregroundChannel)
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

    fun notifyBase(dailyExpenseString: String, expenseString: String) {
        val appContext = MyApp.appContext
        val notificationLayout = RemoteViews(appContext.packageName, R.layout.remote_notification)
        notificationLayout.setTextViewText(R.id.tv_expense_value_daily, dailyExpenseString)
        notificationLayout.setTextViewText(R.id.tv_expense_value_notification, expenseString)
        val manager = NotificationManagerCompat.from(appContext)
        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            appContext, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(appContext, CHANNEL_BASE)
            .setSmallIcon(R.drawable.ic_game)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            // .setCustomBigContentView(notificationExpandLayout)  小米魔改了这部分
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
        if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            manager.notify(BASE_NOTIFICATION_ID, notification)
        } else {
            Log.i(TAG, "notify: 无通知权限")
        }
    }

}