package com.towfikedutips.app

import com.towfikedutips.app.model.Subject
import org.junit.Assert.assertEquals
import org.junit.Test

class ModelUnitTest {
    @Test
    fun testSubjectModel() {
        val subject = Subject(
            id = "test_id",
            name = "Test Subject",
            color = "#FF0000"
        )
        assertEquals("test_id", subject.id)
        assertEquals("Test Subject", subject.name)
        assertEquals("#FF0000", subject.color)
    }
}
