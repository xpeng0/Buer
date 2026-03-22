package com.cscyxp.buer
import org.junit.Assert.assertEquals

fun <T> T.shouldBe(
    expected: T,
    message: String = "",
    isPrint: Boolean = true
) {
    if (isPrint) println("$message Expect: $expected | Actual: $this")
    assertEquals(expected, this)
}