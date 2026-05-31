package com.cscyxp.bookkeeping

import com.cscyxp.bookkeeping.domain.Category
import com.cscyxp.bookkeeping.domain.KeyAction
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
    val regex = Regex("\\[(.*?)]|.")

    regex.findAll(this).forEach { match ->
        val fullMatch = match.value
        val token = if (fullMatch.startsWith("[")) {
            match.groups[1]?.value
        } else {
            fullMatch
        }
        val action = actionMap[token]
        if (action != null) {
            result.add(action)
        }
    }

    return result
}
