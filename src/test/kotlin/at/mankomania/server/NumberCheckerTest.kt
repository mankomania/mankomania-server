package at.mankomania.server

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class NumberCheckerTest {
    @Test
    fun testDescribeNumber() {
        assertEquals("zero", NumberChecker.describeNumber(0))
        assertEquals("positive", NumberChecker.describeNumber(5))
        assertEquals("negative", NumberChecker.describeNumber(-2))
    }
}