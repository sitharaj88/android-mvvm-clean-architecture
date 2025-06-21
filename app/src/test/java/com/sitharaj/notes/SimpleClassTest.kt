package com.sitharaj.notes

import org.junit.Assert.assertEquals
import org.junit.Test

class SimpleClassTest {
    @Test
    fun testAdd() {
        val simple = SimpleClass()
        assertEquals(3, simple.add(1, 2))
    }
}

