package com.coconutmkii.nestjsidea.framework

import org.junit.Assert.*
import org.junit.Test

class NestJSFrameworkTest {

    @Test
    fun `NestJSFramework should be singleton instance`() {
        val instance1 = NestJSFramework.INSTANCE
        val instance2 = NestJSFramework.INSTANCE

        assertSame(instance1, instance2)
        assertNotNull(instance1)
    }

    @Test
    fun `NestJSFramework should have correct ID`() {
        assertEquals("NestjsCLI", NestJSFramework.ID)
    }

    @Test
    fun `NestJSFramework should have presentable name`() {
        val name = NestJSFramework.INSTANCE.presentableName
        assertNotNull(name)
        assertTrue(name.isNotBlank())
    }

    @Test
    fun `NestJSFramework should have icon`() {
        val icon = NestJSFramework.INSTANCE.icon
        assertNotNull(icon)
    }

    @Test
    fun `NestJSFramework should be a FrameworkType instance`() {
        assertTrue(NestJSFramework.INSTANCE is com.intellij.framework.FrameworkType)
    }
}
