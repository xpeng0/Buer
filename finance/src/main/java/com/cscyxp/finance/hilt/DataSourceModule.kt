package com.cscyxp.finance.hilt

import com.cscyxp.finance.StockDatasource
import com.cscyxp.finance.tencent.TencentDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // 全局单例
abstract class DataSourceModule {

    // @Binds注入接口
    @Binds
    @Singleton
    abstract fun bindStockDatasource(
        // 参数：你想要 Hilt 真正去实例化的那个实现类
        tencentDataSource: TencentDataSource
    ): StockDatasource // 返回值：你要绑定的接口
}