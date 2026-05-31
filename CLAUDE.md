# Buer 项目总览

## 项目大图景

Buer 是一个**组件化/多模块** Android 应用，当前处于从单体向多模块演进的过渡期。每一层有严格的自治边界。

### 模块依赖网络

```
:app (壳工程，Application 入口)
 ├── :feature:bookkeeping (记账功能模块)
 │    └── :xpviews (自定义 Compose 图表组件库)
 ├── :finance (理财功能模块)
 │    └── :xpviews
 └── :xpviews
```

依赖方向：**壳工程依赖 feature 模块，feature 模块依赖公共组件库。模块之间互不依赖。**

### 模块自治边界

| 模块 | 类型 | namespace | 核心职责 |
|------|------|-----------|----------|
| `:app` | application | `com.cscyxp.buer` | **首页壳工程**：Application、NavHost 导航枢纽、首页 Dashboard。不持有任何业务逻辑 |
| `:feature:bookkeeping` | library | `com.cscyxp.bookkeeping` | 记账模块：账单列表、记一笔、月度图表统计、分类筛选。独立 Room 数据库 |
| `:finance` | library | `com.cscyxp.finance` | 理财模块：自选股 watchlist、股票搜索、股票详情(K线/分时图)、腾讯股票 API |
| `:xpviews` | library | `com.cscyxp.xpviews` | 纯 UI 组件库：折线图(TrendLineChart)、柱状图、饼图，无业务逻辑 |
| `:feature:fitness` | library | `com.cscyxp.fitness` | (规划中) 健身模块 |

### 模块自治规则

- 每个模块拥有独立的 namespace、独立的 Hilt DI 模块
- feature 模块之间**不得直接相互依赖**；跨模块跳转通过 `:app` 的 NavHost 路由串联
- 公共 UI 组件（如图表）下沉到 `:xpviews`，业务模块仅依赖它，不反向依赖
- 数据库：组件化完成后每个 feature 模块拥有独立的 Room 数据库实例

### 导航架构规范

导航采用 **Compose Navigation 2.8+ 强类型路由（Type-Safe Navigation）**，彻底废除字符串拼接 `composable("route/{arg}")` + `navArgument()` 方式。

**路由定义：**

每个模块在 `navigation` 包下用 `@Serializable` 定义路由类：

```kotlin
// 无参数路由 → object
@Serializable object FinanceWatchlist

// 有参数路由 → data class（参数自动参与序列化）
@Serializable data class FinanceDetail(val symbol: String, val exchange: String)
```

**模块对外暴露导航：**

所有模块统一通过 `NavGraphBuilder` 扩展函数 + `NavController` 扩展函数对外暴露导航。模块内部的路由类和 Screen Composable 一律使用 `internal` 可见性。

```kotlin
// 1. [Module]Routes.kt — @Serializable 路由类定义（一律 internal）
@Serializable internal object FinanceWatchlist
@Serializable internal data class FinanceDetail(val symbol: String, val exchange: String)
@Serializable internal object FinanceSearch

// 2. [Module]Navigation.kt — NavGraphBuilder 扩展 + NavController 扩展
fun NavGraphBuilder.financeNavGraph(navController: NavController) {
    composable<FinanceWatchlist> { /* Screen goes here */ }
    composable<FinanceDetail> { /* Screen */ }
    composable<FinanceSearch> { /* Screen */ }
}

fun NavController.navigateToFinance() {
    navigate(FinanceWatchlist)
}
```

**拥有底部导航栏的模块（如 bookkeeping）：** Tab 壳通过 private Composable 自管理内部 Scaffold + NavigationBar + 嵌套 NavHost，但对外仍通过 `NavGraphBuilder` 扩展注册所有路由（tab 壳入口 + 全屏详情页），与其他模块一致。

```kotlin
fun NavGraphBuilder.bookkeepingNavGraph(navController: NavController) {
    composable<BookkeepingHome> {
        BookkeepingNavHost(
            onBackToHome = { navController.popBackStack() },
            onAddClick = { navController.navigate(BookkeepingAdd) },
            onCategoryChartClick = { categoryId ->
                navController.navigate(BookkeepingCategoryChart(categoryId))
            }
        )
    }
    composable<BookkeepingAdd> {
        AddTransactionScreen(onBackClick = { navController.popBackStack() })
    }
    composable<BookkeepingCategoryChart> { backStackEntry ->
        val route = backStackEntry.toRoute<BookkeepingCategoryChart>()
        CategoryChartScreen(route.categoryId, onBackClick = { navController.popBackStack() })
    }
}

fun NavController.navigateToBookkeeping() {
    navigate(BookkeepingHome) { launchSingleTop = true }
}
```

app 级 NavHost 统一通过扩展函数引入各模块路由：

```kotlin
NavHost(...) {
    composable<Home> { BuerHomeScreen(...) }
    bookkeepingNavGraph(navController)
    financeNavGraph(navController)
}
```

**关键约束：**
- 所有模块统一对外暴露 `NavGraphBuilder` 扩展函数 + `NavController` 扩展函数
- 路由类一律标记 `@Serializable`，使用 `kotlinx-serialization` 序列化
- 模块内部的路由类和 Screen Composable 一律使用 `internal` 可见性
- 模块内部的 tab 壳（如 BookkeepingNavHost）为 `private`，不对外暴露
- 路由注册一律使用泛型 `composable<RouteType>` 写法
- 参数提取一律使用 `backStackEntry.toRoute<RouteType>()`
- 导航跳转使用 `navController.navigate(RouteType(args))`，严禁字符串拼接
- 底部导航栏选中判断使用 `currentDestination?.hasRoute<T>()`
- ViewModel 中获取路由参数使用 `savedStateHandle.toRoute<T>()`

### 转场动画规范

**全局策略：** 每个 `NavHost` 在自身级别统一配置四个转场动画参数，作为该 NavHost 内所有子页面的缺省值。所有有返回栈前后关系的 push/pop 页面自动继承 slide 动画，无需逐个配置。

```kotlin
NavHost(
    navController = navController,
    startDestination = Home,
    enterTransition = {
        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(400))
    },
    exitTransition = {
        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(400))
    },
    popEnterTransition = {
        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400))
    },
    popExitTransition = {
        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(400))
    }
) {
    // ...
}
```

**四个参数的方向语义（Start=左, End=右，LTR 布局下）：**

| 参数 | 方向 | 触发时机 | 效果 |
|------|------|------|------|
| `enterTransition` | `Start` → 位置 | push 进入 | 新页从右侧滑入 |
| `exitTransition` | 位置 → `Start` | push 离开 | 旧页向左滑出 |
| `popEnterTransition` | `End` → 位置 | pop 返回 | 旧页从左侧滑回 |
| `popExitTransition` | 位置 → `End` | pop 离开 | 当前页向右滑出 |

## 技术栈死锁

以下技术栈为项目硬性标准，不得引入替代方案：

| 领域 | 技术选型 | 版本 |
|------|----------|------|
| 语言 | Kotlin | 2.0.21 |
| 构建 | Gradle (Kotlin DSL) + Version Catalog | 8.11.1 / AGP 8.9.1 |
| UI | Jetpack Compose (Material 3) | BOM 2026.04.01 |
| 异步 | Kotlin Coroutines + Flow | 1.9.0 |
| DI | Hilt (Dagger) | 2.57.1 |
| 数据库 | Room | 2.8.0 |
| 网络 | Retrofit + OkHttp + Gson | 2.9.0 / 4.11.0 |
| 导航 | Compose Navigation (NavHost) | — |
| 序列化 | kotlinx.serialization | 1.9.0 |
| 编译 | KSP (非 kapt) | 2.0.21-1.0.25 |
| 测试 | JUnit 5 + MockK + Turbine | - |

### 严禁引入

- 禁止 XML 布局（所有页面必须 Compose）
- 禁止 Fragment + XML nav_graph（统一使用 Compose NavHost）
- 禁止 kapt（全部使用 KSP）
- 禁止 RxJava / LiveData（统一 Flow）
- 禁止 Koin（统一 Hilt）

## 代码洁癖

- 所有 Composable 组件必须显式声明 `modifier: Modifier = Modifier` 作为第一个可选参数
- 严禁在 Composable 内部直接使用 `rememberCoroutineScope()` 触发非点击副作用 — 统一使用 `LaunchedEffect`
- 每个 Composable 组件写一个 `@Preview` 预览函数，命名为 `{组件名}Preview`
- ViewModel 中 StateFlow 一律只读暴露（`val`），修改通过 `private val _xxx = MutableStateFlow()` 进行
- sealed class 用于 UiState，命名规范：`{功能名}UiState`，子类用 `Loading`/`Success`/`Error` 或用具体语义名
- 数据类不可变：全部 `val`，禁止 `var`
- **非必要不使用 `Scaffold`**：Scaffold 仅在模块级 NavHost 中作为"全局单壳骨架"使用（提供底部 NavigationBar）。各功能子页面（Screen Composable）一律禁止套 Scaffold，避免多重 Padding 套娃。TopBar 用 `Row` + `TextButton` 手动实现，FAB 用 `Box` + `Modifier.align(Alignment.BottomEnd)` 手动定位

---

## 模块详解

### 1. `:app` — 壳工程

**包结构：**
```
com.cscyxp.buer/
├── MyApp.kt                    # @HiltAndroidApp Application 入口
├── MainActivity.kt             # 主 Activity，setContent { BuerApp() }
├── ViewExt.kt                  # 通用扩展函数（format2f, toggleSelection）
├── home/ui/
│   └── BuerHomeScreen.kt      # 首页 Compose Dashboard
├── navigation/
│   ├── AppRoutes.kt            # 顶层路由定义（Home, BookkeepingGraph）
│   └── AppNavGraph.kt          # 顶层 NavHost，编排各模块入口
├── db/
│   ├── AppDataBase.kt          # Room 数据库（仅 WatchlistEntity，供 finance 过渡使用）
│   └── Migration.kt            # 数据库迁移
├── hilt/
│   └── UtilModule.kt           # DI：WatchlistDao provider（桥接 finance DAO 到 AppDataBase）
└── utils/
    ├── NotificationUtil.kt     # 通知渠道管理
    └── PermissionUtil.kt       # 权限工具
```

**核心功能：**
- 首页 Dashboard（BuerHomeScreen）：理财、记账、番茄钟、工具入口卡片
- 全局导航枢纽：通过 Compose NavHost 串联各 feature 模块

**导航结构：**
```
BuerApp() NavHost (startDestination=Home)
├── composable<Home>              → BuerHomeScreen（首页 Dashboard）
├── composable<BookkeepingGraph>  → BookkeepingNavHost（记账模块入口）
└── financeNavGraph(navController)  → 理财模块子图
```

---

### 2. `:feature:bookkeeping` — 记账模块

**包结构：**
```
com.cscyxp.bookkeeping/
├── domain/
│   ├── Category.kt              # 分类模型
│   ├── Transaction.kt           # 交易模型
│   ├── DailyTransaction.kt      # 每日交易汇总
│   ├── TransactionFilter.kt     # 交易筛选条件
│   ├── CategoryChart.kt         # 分类图表数据
│   ├── KeyAction.kt             # 数字键盘动作枚举
│   └── HomeUiState.kt           # 列表页 UI 状态
├── data/
│   ├── entity/
│   │   ├── TransactionEntity.kt
│   │   ├── CategoryEntity.kt
│   │   ├── CategoryEntityWithChildren.kt
│   │   └── TransactionEntityWithCategoryEntity.kt
│   ├── dao/
│   │   ├── TransactionDao.kt
│   │   └── CategoryDao.kt
│   └── repository/
│       ├── TransactionRepository.kt
│       └── CategoryRepository.kt
├── db/
│   └── BookkeepingDatabase.kt   # 独立 Room 数据库（bookkeeping.db）
├── di/
│   └── BookkeepingModule.kt     # Hilt DI：Clock, Database, DAOs
├── vm/
│   ├── TransactionListViewModel.kt  # 交易列表 VM
│   ├── AddViewModel.kt              # 新增交易 VM
│   └── ChartViewModel.kt            # 图表统计 VM
├── ui/
│   ├── TransactionListScreen.kt     # 交易列表页（Compose）
│   ├── AddTransactionScreen.kt      # 记一笔页（Compose）
│   ├── ChartScreen.kt               # 统计图表页（Compose）
│   └── CategoryChartScreen.kt       # 分类详情页（Compose）
├── navigation/
│   ├── BookkeepingRoutes.kt         # @Serializable 路由定义（internal）
│   └── BookkeepingNavHost.kt        # 自包含 NavHost（Scaffold + BottomBar + 嵌套 NavHost）
└── util/
    ├── Mappers.kt               # Entity ↔ Domain 转换 + format2f 扩展
    ├── TimeHelper.kt            # 时间计算工具
    ├── RawUtil.kt               # 从 raw/categories.json 加载初始分类
    └── BookkeepingAppContext.kt # 模块级 Application Context
```

**核心功能：**
- 交易列表：月度收支汇总 + 分类筛选 + 每日交易卡片
- 记一笔：分类选择（HorizontalPager 分页网格）+ 数字键盘输入 + 日期选择
- 图表统计：近6月柱状图（:xpviews BarChart）+ 当月饼图（PieChart）+ 分类详情
- 独立数据库：`BookkeepingDatabase` 独立实例 `bookkeeping.db`，版本 1，不与 `:app` 共享

**导航子图（自包含）：**
```
BookkeepingNavHost (Scaffold + NavigationBar)
├── BookkeepingList (start)  → 账单列表 | BackHandler → onBackToHome()
├── BookkeepingAdd           → 记一笔
├── BookkeepingChart         → 统计图表 | BackHandler → onBackToHome()
└── BookkeepingCategoryChart → 分类详情（args: categoryId）
```

**包结构：**
```
com.cscyxp.finance/
├── StockDatasource.kt          # 股票数据源接口（抽象）
├── StockExchange.kt            # 交易所枚举（SHANG_HAI / SHEN_ZHEN）
├── StockMathUtil.kt / Ext.kt   # 数学工具 + 扩展函数
├── dao/
│   └── WatchlistDao.kt         # 自选股 Room DAO（操作 :app 的 AppDataBase）
├── db/
│   └── WatchlistConvert.kt     # Entity ↔ Domain 转换
├── entity/
│   ├── StockKey.kt             # 股票唯一标识 (symbol + exchange)
│   ├── StockEntity.kt          # K 线数据实体
│   ├── StockInfo.kt            # 搜索返回的股票信息
│   ├── StockMinute.kt          # 分时数据
│   ├── StockQuotation.kt       # 实时行情
│   ├── WatchStock.kt           # 自选股 Domain 模型
│   └── WatchlistEntity.kt      # 自选股 Room Entity
├── repository/
│   └── StockRepository.kt      # 股票数据仓库核心（自选股 CRUD + 缓存策略 + 轮询调度）
├── tencent/
│   ├── TencentStockApi.kt      # Retrofit 接口（腾讯股票 API）
│   ├── TencentDataSource.kt    # StockDatasource 实现
│   ├── TencentStockResponse.kt # 网络返回的原始 JSON 模型
│   ├── TencentStockDetail.kt   # 详情接口返回模型
│   ├── TencentKLineEntity.kt   # K 线接口返回模型
│   └── TencentStockUtil.kt     # 腾讯数据解析工具
├── hilt/
│   ├── FinanceNetworkModule.kt # 网络层 DI（OkHttpClient, Retrofit, TencentStockApi）
│   ├── DataSourceModule.kt     # 接口绑定 DI（StockDatasource → TencentDataSource）
│   └── DispatchersModule.kt    # CoroutineDispatcher 注入（@IoDispatcher 限定符）
├── watchlist/
│   ├── WatchlistPreloader.kt   # 自选股数据预热器（在 MainActivity idle 时触发）
│   ├── vm/WatchlistViewModel.kt
│   └── ui/
│       ├── fragment/WatchlistFragment.kt      # ComposeView 桥接 Fragment
│       ├── composable/StockWatchlistScreen.kt # 自选股列表 Compose
│       ├── adapter/WatchlistAdapter.kt        # RecyclerView Adapter (旧)
│       └── state/
│           ├── WatchlistUiState.kt
│           └── StockItemUiState.kt
├── search/
│   ├── vm/SearchViewModel.kt
│   └── ui/
│       ├── fragment/SearchFragment.kt
│       └── state/
│           ├── SearchScreenUiState.kt   # sealed: SearchBoard / SearchResult
│           ├── SearchResultState.kt     # sealed: Loading / Success / Error
│           ├── StockSearchItemUiState.kt
│           └── StockTag.kt
├── details/
│   ├── vm/StockDetailViewModel.kt
│   └── ui/
│       ├── StockDetailFragment.kt
│       ├── composable/StockDetailScreen.kt
│       └── state/
│           └── StockDetailUiState.kt    # sealed: Loading / Success / Error
└── res/navigation/
    └── finance_nav_graph.xml             # 理财模块导航子图
```

**核心功能：**
- **自选股列表**：Room 持久化 + 实时行情轮询（5 秒间隔）+ 缓存策略（10 分钟过期）
- **股票搜索**：debounce 300ms 输入防抖 + 搜索结果与自选状态联合展示
- **股票详情**：分时图（TrendLineChart 带触摸交互）+ 实时价格/涨跌幅 + K 线（规划中）
- **数据预热**：WatchlistPreloader 在 MainActivity IdleHandler 中异步启动，避免卡首页

**导航子图：**
```
finance_nav_graph (startDestination=watchlistFragment)
├── watchlistFragment → action_to_searchFragment
│                     → action_to_detailFragment (args: stockKey)
├── searchFragment    → action_to_detailFragment (args: stockKey)
└── detailFragment    (args: stockKey)
```

**数据流架构（MVI）：**
```
Fragment (ComposeView) → Screen Composable → event callback → ViewModel.method()
                                                                    ↓
DataSource (interface) → TencentDataSource (Retrofit impl) → StockRepository (cache + polling)
                                                                    ↓
                                                            StateFlow<UiState> → Composable 重组
```

---

### 3. `:xpviews` — 自定义图表组件库

**包结构：**
```
com.cscyxp.xpviews/
├── BarChartView.kt             # 自定义 View 柱状图（旧，XML 时代产物）
├── PieChartView.kt             # 自定义 View 饼图（旧）
├── TrendLineView.kt            # 自定义 View 折线图（旧）
├── ViewExt.kt                  # View 扩展工具
└── composable/
    ├── TrendLineChart.kt       # Compose 折线图（带贝塞尔曲线 + 触摸指示虚线）
    ├── BarChart.kt             # Compose 柱状图
    └── PieChart.kt             # Compose 饼图
```

**核心功能：**
- `TrendLineChart`：纯 Compose Canvas 折线图，支持贝塞尔曲线平滑、渐变色、触摸交互（pointerInput 拖拽选中数据点）
- 旧版自定义 View 组件（Bar/Pie/TrendLine）即将废弃，待迁移至 Compose

---

### 4. `:feature:fitness` — 健身模块（规划中）

待创建。应作为独立 library module，拥有自己的：
- Room 数据库（独立实例，不与 :app 共享）
- 导航子图
- Hilt DI 模块
- 与 :app 通过 navigation action 交互，不直接依赖其他 feature 模块

---

## 数据库架构

**当前状态：** 记账模块已拥有独立的 `BookkeepingDatabase`。app 层 `AppDataBase` 仅保留 `WatchlistEntity` 供 finance 模块过渡使用。

| 数据库 | 所属模块 | 文件 | entities | 版本 |
|--------|----------|------|----------|------|
| `BookkeepingDatabase` | `:feature:bookkeeping` | `bookkeeping.db` | TransactionEntity, CategoryEntity | 1 |
| `AppDataBase` | `:app` | `buer_database` | WatchlistEntity（过渡至 Phase 4） | 5 |

**目标：** Phase 4 将 `WatchlistEntity` 从 `AppDataBase` 迁移到 `:finance` 独立数据库，完全废除跨模块 DAO 访问。

---

## 当前状态 & 技术债务

| 项目 | 现状 | 目标 |
|------|------|------|
| 记账功能 | COMPLETE — 已迁移到 `:feature:bookkeeping` + Compose | ✓ |
| 首页 | Compose，位于 `:app` | 保留在 `:app` 壳工程 |
| 理财模块 | Compose + Fragment 桥接，MVI 模式 | 去掉 Fragment 壳，接入 NavHost（Phase 3） |
| 数据库 | app 层仅剩 WatchlistEntity，bookkeeping 已独立 | `:finance` 独立数据库（Phase 4） |
| 导航 | Compose NavHost + Type-Safe Routes | ✓ |
| 图表 | 旧自定义 View + 新 Compose 混用 | 全部迁移到 `:xpviews` Compose 组件 |
| 网络 | 仅 `:finance` 有 Retrofit | 抽取公共网络层到 `:core:network` |
| Hilt 模块 | 分散在各模块，app UtilModule 桥接 finance DAO | Phase 4 移除 UtilModule 桥接代码 |
