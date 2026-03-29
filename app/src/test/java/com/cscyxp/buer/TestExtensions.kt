package com.cscyxp.buer
import org.junit.jupiter.api.Assertions.assertEquals

fun <T> T.shouldBe(
    expected: T,
    message: String = "",
    isPrint: Boolean = true
) {
    if (isPrint) println("$message Expect: $expected | Actual: $this")
    assertEquals(expected, this)
}

fun String.toKeyActions(): List<KeyAction> {
    val result = mutableListOf<KeyAction>()
    val actionMap = KeyAction.entries.associateBy { it.keyName }
    // 正则表达式解释：
    // \[(.*?)]  -> 匹配方括号内的内容，并捕获括号里的文本（如 del）
    // |         -> 或者
    // .         -> 匹配任意单个字符（如 1, 2, .）
    val regex = Regex("\\[(.*?)]|.")

    regex.findAll(this).forEach { match ->
        val fullMatch = match.value
        val token = if (fullMatch.startsWith("[")) {
            // 如果是 [del]，则取出捕获组里的 "del"
            match.groups[1]?.value
        } else {
            // 如果是普通字符 "1"，直接用它
            fullMatch
        }
        val action = actionMap[token]

        if (action != null) {
            result.add(action)
        }
    }

    return result
}