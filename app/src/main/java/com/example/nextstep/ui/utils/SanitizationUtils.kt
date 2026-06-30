package com.example.nextstep.ui.utils

object SanitizationUtils {

    fun sanitizeEmail(value: String): String {
        return value
            .trim()
            .filter { c -> isValidEmailChar(c) }
            .lowercase()
    }

    fun sanitizePassword(value: String): String {
        return value
            .trim()
            .filter { c -> isValidPasswordChar(c) }
    }

    private fun isValidEmailChar(c: Char): Boolean {
        return c != '\n' && c != '\r' && c != '\t' && c != ' ' && !isInvisibleChar(c)
    }

    private fun isValidPasswordChar(c: Char): Boolean {
        return c != '\n' && c != '\r' && c != '\t' && !isInvisibleChar(c)
    }

    fun isInvisibleChar(c: Char): Boolean {
        val type = Character.getType(c)
        return type.toByte() == Character.CONTROL ||
                type.toByte() == Character.FORMAT ||
                type.toByte() == Character.PRIVATE_USE ||
                type.toByte() == Character.SURROGATE ||
                type.toByte() == Character.UNASSIGNED ||
                (c.code in 0x200B..0x200F) ||
                (c.code in 0x2028..0x202E) ||
                (c.code in 0xFE00..0xFE0F) ||
                c.code == 0xFEFF ||
                c.code == 0x00AD ||
                (c.code in 0x2060..0x2064) ||
                (c.code in 0xFFF0..0xFFF8) ||
                (c.code in 0xE0000..0xE007F)
    }
}
