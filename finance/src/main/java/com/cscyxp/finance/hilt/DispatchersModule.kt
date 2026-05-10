package com.cscyxp.finance.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher // 🌟 专门给 IO 用的标签

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatcher // 🌟 专门给 Default 用的标签

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

    @Provides
    @IoDispatcher // 🌟 贴上标签
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @DefaultDispatcher // 🌟 贴上标签
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @Singleton // 保证全局只有一个实例
    @ApplicationScope // 🌟 贴上我们刚刚发明的专属标签！
    fun providesApplicationScope(): CoroutineScope {
        // 🌟 这是极其重要的一句代码：
        // 1. SupervisorJob() 确保如果某个后台任务崩溃，不会把整个全局作用域炸毁（防连坐）。
        // 2. Dispatchers.Default 适合后台计算逻辑。
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}