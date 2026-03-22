package com.cscyxp.buer

import android.app.Application
import android.content.Context
import com.cscyxp.buer.utils.NotificationUtil
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        NotificationUtil.createNotificationChannels()
    }
    companion object {
        lateinit var appContext: Context
            private set
    }
}
