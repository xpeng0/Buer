package com.cscyxp.buer.navigation

import kotlinx.serialization.Serializable

/**
 * App 模块顶层路由定义。
 * 每个 feature 模块对外仅暴露一个入口路由，内部导航由模块自行管理。
 */
@Serializable object Home