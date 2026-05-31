package com.cscyxp.buer

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.cscyxp.buer.navigation.BuerApp
import com.cscyxp.finance.watchlist.WatchlistPreloader
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var watchlistPreloader: Lazy<WatchlistPreloader>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 首页渲染完毕后触发理财数据预热
        window.decorView.post {
            lifecycleScope.launch(Dispatchers.Default) {
                val preloader = watchlistPreloader.get()
                preloader.preheat(20)
            }
        }

        setContent {
            BuerApp()
        }
    }
}
