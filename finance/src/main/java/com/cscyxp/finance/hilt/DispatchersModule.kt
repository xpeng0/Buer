package com.cscyxp.finance.hilt

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher // 🌟 专门给 IO 用的标签

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatcher // 🌟 专门给 Default 用的标签

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

    @Provides
    @IoDispatcher // 🌟 贴上标签
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @DefaultDispatcher // 🌟 贴上标签
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}