package com.cscyxp.finance.tencent

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface TencentStockApi {

    // 强烈建议用 https，否则 Android 9.0 以上会报 Cleartext HTTP 错误
    @GET("appstock/app/fqkline/get")
    suspend fun getKLineData(
        @Query("param") param: String
    ): TencentStockResponse

    @GET
    suspend fun getQt(
        @Url url: String
    ): ResponseBody

    @GET
    suspend fun fuzzySearchStockInfo(
        @Url url: String
    ): ResponseBody

}