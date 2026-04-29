package com.cscyxp.finance.util

import java.io.InputStreamReader

object JsonUtil {
     // 读取 src/test/resources 下的文件内容
     fun readFileFromResources(fileName: String): String {
         val inputStream = javaClass.classLoader?.getResourceAsStream(fileName)
             ?: throw IllegalArgumentException("找不到文件: $fileName")
         val reader = InputStreamReader(inputStream, "UTF-8")
         return reader.readText()
     }
}