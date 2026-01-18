package com.coconutmkii.nestjsidea.framework.manager

import org.junit.Assert.*
import org.junit.Test

class PackageManagerTest {

    @Test
    fun `PackageManager enum should have all expected values`() {
        val managers = PackageManager.entries.toTypedArray()
        assertEquals(3, managers.size)
        
        assertTrue(managers.contains(PackageManager.NPM))
        assertTrue(managers.contains(PackageManager.YARN))
        assertTrue(managers.contains(PackageManager.PNPM))
    }

    @Test
    fun `PackageManager should have correct labels`() {
        assertEquals("npm", PackageManager.NPM.label)
        assertEquals("yarn", PackageManager.YARN.label)
        assertEquals("pnpm", PackageManager.PNPM.label)
    }

    @Test
    fun `PackageManager labels should be lowercase`() {
        PackageManager.entries.forEach { manager ->
            assertEquals(
                "Label for ${manager.name} should be lowercase",
                manager.label,
                manager.label.lowercase()
            )
        }
    }

    @Test
    fun `PackageManager should be serializable and comparable`() {
        val npm1 = PackageManager.NPM
        val npm2 = PackageManager.NPM
        val yarn = PackageManager.YARN

        assertEquals(npm1, npm2)
        assertNotEquals(npm1, yarn)
        assertEquals(npm1.hashCode(), npm2.hashCode())
    }
}
