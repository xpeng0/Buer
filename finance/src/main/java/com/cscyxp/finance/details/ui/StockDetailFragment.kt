package com.cscyxp.finance.details.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.cscyxp.finance.details.ui.composable.StockDetailScreenRoute

class StockDetailFragment: Fragment(

) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return ComposeView(requireContext()).apply {

            // 2. 🌟 极其重要！大厂架构规范：设置合理的组合销毁策略
            // 这能保证当 Fragment 视图被销毁（比如退栈）时，里面的 Compose 协程和内存被及时释放，防止内存泄漏
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )


            // 3. 注入你的 Compose 路由入口

            setContent {
                StockDetailScreenRoute()
            }
        }
    }
}