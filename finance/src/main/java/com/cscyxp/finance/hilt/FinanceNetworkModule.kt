package com.cscyxp.finance.hilt
import com.cscyxp.finance.tencent.TencentStockApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // 绑定到全局单例生命周期
object FinanceNetworkModule {

    // 1. 提供 OkHttpClient
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    // 2. 提供 Retrofit (Hilt 会自动把上面的 OkHttpClient 传进来)
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://ifzq.gtimg.cn/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 3. 提供最终的 Api 接口 (Hilt 会自动把上面的 Retrofit 传进来)
    @Provides
    @Singleton
    fun provideTencentStockApi(retrofit: Retrofit): TencentStockApi {
        return retrofit.create(TencentStockApi::class.java)
    }

}