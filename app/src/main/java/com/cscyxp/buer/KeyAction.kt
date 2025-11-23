package com.cscyxp.buer

enum class KeyAction(
    val keyName: String,
    val row: Int,
    val column: Int,
    val rowSpan: Int = 1,
    val colSpan: Int = 1
) {
    DIGIT_7("7", 0, 0),
    DIGIT_8("8", 0, 1),
    DIGIT_9("9", 0, 2),
    DATE("date", 0, 3),
    DIGIT_4("4", 1, 0),
    DIGIT_5("5", 1, 1),
    DIGIT_6("6",1 ,2),
    DIGIT_1("1", 2, 0),
    DIGIT_2("2", 2, 1),
    DIGIT_3("3", 2, 2),
    DOT(".", 3, 0),
    DIGIT_0("0", 3, 1),
    DELETE("del", 3, 2),
    OK("ok",1, 3, 3, 1);

    companion object {
        val LAYOUT_ORDER = listOf(
            DIGIT_7, DIGIT_8, DIGIT_9, DATE,
            DIGIT_4, DIGIT_5, DIGIT_6,
            DIGIT_1, DIGIT_2, DIGIT_3,
            DOT, DIGIT_0, DELETE, OK
        )
    }
}