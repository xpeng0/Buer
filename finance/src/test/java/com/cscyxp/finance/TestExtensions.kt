package com.cscyxp.finance
import org.junit.jupiter.api.Assertions.assertEquals

fun <T> T?.shouldBe(
    expected: T,
    message: String = "",
    isPrint: Boolean = true
) {
    if (isPrint) {
        println(message)
        println("Expect: $expected")
        println("Actual: $this")
    }
    assertEquals(expected, this)
}