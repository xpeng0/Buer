# Buer 组件化重构计划

## 目标

将当前半单体项目重构为清晰的组件化/多模块架构，支持后续健身模块独立接入。

### 现状 vs 目标

| 当前状态 | 目标状态 |
|----------|----------|
| `:app` (壳+记账+DB) | `:app` (仅首页 + 导航枢纽) |
| `:finance` | `:feature:finance` |
| `:xpviews` | `:lib:xpviews` (重命名) |
| — | `:core:database` (新增) |
| — | `:core:network` (新增) |
| — | `:feature:bookkeeping` (新增) |
| — | `:feature:fitness` (新增) |

### 目标依赖图

```
:app
 ├── :feature:finance
 │    ├── :core:network
 │    ├── :core:database
 │    └── :lib:xpviews
 ├── :feature:bookkeeping
 │    ├── :core:database
 │    └── :lib:xpviews
 └── :feature:fitness
      └── :core:database
```

---

## Phase 1: XML → Compose + NavHost 统一导航 ✅ 已完成

**目标：消灭所有 XML 布局和 Fragment+nav_graph 导航，全部换成 Compose + NavHost 路由。**

这是整个重构的前置条件 — Compose 化之后再拆模块，不用拖着 XML 资源文件到处迁移。

### [1.1] `:app` 记账页面 Compose 化

| 现有 (XML + RecyclerView) | 改为 |
|---------------------------|------|
| `TransactionFragment` + `fragment_transaction.xml` + `TransactionAdapter` | Composable `TransactionListScreen` (LazyColumn) |
| `AddFragment` + `fragment_add.xml` | Composable `AddTransactionScreen` |
| `ChartFragment` + `fragment_chart.xml` | Composable `ChartScreen`（使用 `:xpviews` 组件） |
| `CategoryChartFragment` + `fragmnet_category_chart.xml` + 若干 Adapter | Composable `CategoryChartScreen` |
| `dialog_category_picker.xml` / `dialog_month_picker.xml` | Composable Dialog |
| 所有 `*Adapter.kt`（RecyclerView） | 删除，LazyColumn 内直接渲染 |

### [1.2] `:finance` 页面去 Fragment 化

| 现有 (Fragment + ComposeView) | 改为 |
|-------------------------------|------|
| `WatchlistFragment` + ComposeView 桥接 | 纯 Composable，NavHost 路由直达 |
| `SearchFragment` + ComposeView 桥接 | 纯 Composable，NavHost 路由直达 |
| `StockDetailFragment` + ComposeView 桥接 | 纯 Composable，NavHost 路由直达 |

`:finance` 内部原有 Compose Screen（`StockWatchlistScreen`, `StockDetailScreen`）基本不动，只去掉 Fragment 壳。

### [1.3] `:app` 首页去 Fragment 化

| 现有 | 改为 |
|------|------|
| `HomeFragment` + ComposeView 桥接 | 纯 Composable `BuerHomeScreen`，NavHost 路由直达 |

### [1.4] NavHost 统一导航

- 删除所有 `res/navigation/*.xml`（`nav_graph.xml`, `finance_nav_graph.xml`）
- 删除 `build.gradle.kts` 中的 `navigation-safeargs-kotlin` 插件
- `MainActivity` 改为纯 Compose `setContent { NavHost(...) }`
- 路由定义用 sealed class 或 Kotlin DSL（`composable("route") { ... }`）
- 底部导航栏用 Compose `NavigationBar`（替换当前 XML `bottom_nav_menu.xml` + `BottomNavigationView`）
- 跨模块路由：`:app` 持有路由表，通过 lambda 回调或路由常量串联各模块

### [1.5] 删除 `viewBinding = true`

- 所有 Fragment + XML 布局消失后，`buildFeatures { viewBinding = true }` 从所有模块中移除

### 实际完成的内容

- 新增 Composable Screens: `BuerHomeScreen`, `TransactionListScreen`, `AddTransactionScreen`, `ChartScreen`, `CategoryChartScreen`, `SearchScreen`, `BarChart`/`PieChart` (xpviews)
- `:finance` 模块导航: `FinanceNavigation.kt`（`NavGraphBuilder.financeNavGraph()` + `NavController.navigateToFinance()`）
- 根导航: `AppNavGraph.kt`（`BuerApp()` with `Scaffold` + `NavigationBar` + `NavHost`）
- 删除 9 Fragments, 10 Adapters, 19 XML layouts, 3 navigation/menu XML
- `app/build.gradle.kts` 和 `finance/build.gradle.kts` 清理: safeargs, viewBinding, navigation-fragment/ui, fragment.ktx
- `MainActivity` 改为 `setContent { BuerApp() }`
- 保留: `remote_notification.xml`（系统通知需要 RemoteViews）

**Phase 1 实际耗时：大** ✅

---

## Phase 2: 记账功能抽离为 `:feature:bookkeeping`

**目标：`:app` 模块仅保留首页和全局导航逻辑，记账业务全部移到独立模块。**

此时记账页面已经是纯 Compose，迁移不再涉及 XML 资源。

### [2.1] 创建 `:feature:bookkeeping` 模块
- namespace: `com.cscyxp.bookkeeping`
- 应用 android library 插件 + Hilt + KSP + Compose Compiler
- 初始依赖（此时 `:core:database` 尚未创建，先直接依赖 Room）：`:lib:xpviews`

### [2.2] 迁移业务代码

| 类别 | 文件 |
|------|------|
| 模型 | `Transaction.kt`, `TransactionFilter.kt`, `DailyTransaction.kt`, `Category.kt` |
| 仓库 | `TransactionRepository.kt`, `CategoryRepository.kt` |
| ViewModel | `AddViewModel.kt`, `ChartViewModel.kt`, `MainViewModel.kt`（拆分，记账部分移走） |
| 页面 | `TransactionListScreen`, `AddTransactionScreen`, `ChartScreen`, `CategoryChartScreen`（Phase 1 产物） |
| 工具 | `TimeHelper.kt`, `KeyAction.kt`, `RawUtil.kt` |

### [2.3] `:app` 中的残留处理
- `MainViewModel.kt` 拆分：首页汇总数据（`todayExpend`, `monthExpend`）改为跨模块接口获取，或保留在 `:app` 通过依赖 `:feature:bookkeeping` 的 Repository 获取
- `:app` 删除所有记账相关代码，仅保留 `BuerHomeScreen` + `MainActivity` + NavHost + 底部导航栏

### [2.4] 登录/启动逻辑
- `MainActivity` 的 `RawUtil.loadCategoriesFromRaw()` 初始化逻辑移入 `:feature:bookkeeping` 模块的初始化入口

**Phase 2 预计耗时：中**

---

## Phase 3: 基础设施层（core 模块抽取）

### [3.1] 创建 `:core:database` — 通用数据库基础模块
- 提供 RoomDatabase 抽象基类 / 扩展
- 提供通用 TypeConverter（如日期转换）
- 不包含任何业务 Entity（空壳，供 feature 模块各自创建独立数据库实例）
- 依赖：Room、KSP
- 预计耗时：**小**

### [3.2] 创建 `:core:network` — 通用网络层
- 从 `:feature:finance/hilt/FinanceNetworkModule.kt` 提取通用部分
- 提供 OkHttpClient 通用配置（日志拦截器、超时等）
- 提供 Retrofit.Builder 基础工厂（不含具体 baseUrl 和 Api 接口）
- 各 feature 模块注入后自行 `create(Api::class.java)`
- 依赖：Retrofit、OkHttp、Gson
- 预计耗时：**中**

### [3.3] 重命名 `:xpviews` → `:lib:xpviews`
- 纯重命名，无逻辑变更
- 更新所有模块的 `build.gradle.kts` 依赖引用
- 预计耗时：**小**

---

## Phase 4: 数据库解耦（各模块独立 Room）

**目标：每个 feature 模块拥有独立的 Room 数据库实例，彻底消除跨模块 DAO 访问。**

### [4.1] `:feature:bookkeeping` 独立数据库
- 创建 `BookkeepingDatabase`（仅含 `TransactionEntity` + `CategoryEntity`）
- 创建 Hilt `DatabaseModule` 提供数据库实例
- 从 `:app/AppDataBase` 中移除这两个 Entity

### [4.2] `:feature:finance` 独立数据库
- 创建 `FinanceDatabase`（仅含 `WatchlistEntity`）
- 创建 Hilt `DatabaseModule` 提供数据库实例
- 删除 `:app` 中 `provideFinanceWatchlistDao()` 的桥接代码
- 从 `:app/AppDataBase` 中移除 `WatchlistEntity`

### [4.3] 清理 `:app` 遗留数据库
- `AppDataBase` 如果没有自己的 Entity 则直接删除
- 移除 `MyApp.Companion.appContext` 静态持有
- 移除 `:app/hilt/UtilModule` 中不再需要的 Provider

### [4.4] 数据迁移策略
- `:feature:bookkeeping` 首次启动时检查并迁移旧 `:app` 数据库中的记账数据
- `:feature:finance` 首次启动时检查并迁移旧 `:app` 数据库中的自选股数据
- 迁移完成后标记完成，后续不再检查

**Phase 4 预计耗时：中**

---

## Phase 5: `:app` 壳工程收尾

### [5.1] `:app` 最终保留清单

| 文件 | 职责 |
|------|------|
| `MyApp.kt` | `@HiltAndroidApp` Application 入口 |
| `MainActivity.kt` | `setContent { NavHost(...) }` 导航宿主 |
| `BuerHomeScreen.kt` | 首页 Dashboard（纯 Compose） |
| 底部导航栏 | Compose `NavigationBar` |
| 根路由表 | 聚合各模块路由的 sealed class / constants |
| `NotificationUtil.kt`, `PermissionUtil.kt` | 全局工具（后续可考虑下沉到 `:core:common`） |

### [5.2] 验证
- `:app` 不再包含任何 Room Entity、DAO、Repository
- `:app` 不再包含任何记账/理财业务页面代码
- 所有 feature 页面通过 NavHost 路由串联

**Phase 5 预计耗时：小**

---

## Phase 6: 健身模块搭建

### [6.1] 创建 `:feature:fitness` 模块
- namespace: `com.cscyxp.fitness`
- 依赖：`:core:database`、`:lib:xpviews`
- 拥有独立 Room 数据库、Hilt DI、Compose 页面

### [6.2] 首页入口接入
- `BuerHomeScreen` 中健身卡片 onClick → NavHost 路由跳转

**Phase 6 预计耗时：中**

---

## 任务优先级总览

```
先做（阻塞后续，必须最先）
  ✅ Phase 1.1~1.5  XML → Compose + NavHost

然后（业务解耦）
  ✅ Phase 2.1~2.4  记账功能抽离 :feature:bookkeeping

基础设施（为模块独立提供服务）
  ✅ Phase 3.1~3.3  :core:database, :core:network, :xpviews 重命名

数据库解耦（模块独立 Room）
  ✅ Phase 4.1~4.4  各模块数据库拆分 + 数据迁移

收尾
  ✅ Phase 5.1~5.2  :app 壳工程清理验证

新功能
  ✅ Phase 6.1~6.2  健身模块搭建
```

---

## 风险点

| 风险 | 说明 |
|------|------|
| 工时最大 | Phase 1 Compose 迁移覆盖 :app 和 :finance 所有页面，是最大工作量 |
| 导航重构 | Fragment nav_graph → NavHost 涉及路由改造，跨模块路由需提前设计接口 |
| 数据丢失 | Phase 4 数据库拆分涉及旧数据迁移，需充分测试迁移逻辑 |
| 首页汇总 | MainViewModel 中今日/本月支出汇总依赖记账数据，抽离后需设计跨模块数据获取方案 |