package com.coconutmkii.nestjsidea.framework.file.validator

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class NoWhitespaceValidatorTest {

    private val validator = NoWhitespaceValidator()

    @Test
    fun `should reject null or blank input`() {
        assertFalse(validator.checkInput(null))
        assertFalse(validator.checkInput(""))
        assertFalse(validator.checkInput("   "))
    }

    @Test
    fun `should reject input with whitespace`() {
        assertFalse(validator.checkInput("My Class"))
        assertFalse(validator.checkInput("Hello\tWorld"))
        assertFalse(validator.checkInput("Line\nBreak"))
    }

    @Test
    fun `should accept valid input`() {
        assertTrue(validator.checkInput("MyClass"))
        assertTrue(validator.checkInput("SomeClass123"))
        assertTrue(validator.checkInput("_ClassName"))
    }

    @Test
    fun `canClose delegates to checkInput`() {
        assertTrue(validator.canClose("ValidName"))
        assertFalse(validator.canClose("Invalid Name"))
    }

    @Test
    fun `errorText is not null`() {
        val error = validator.getErrorText("Invalid Name")
        assertNotNull(error)
        assertTrue(error!!.isNotBlank())
    }
}
