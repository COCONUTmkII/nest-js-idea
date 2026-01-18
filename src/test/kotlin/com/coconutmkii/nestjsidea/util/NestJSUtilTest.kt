package com.coconutmkii.nestjsidea.util

import org.junit.Assert.*
import org.junit.Test

class NestJSUtilTest {

    @Test
    fun `isNestJsonFile should return true for valid nest-cli json file names`() {
        assertTrue(isNestJsonFile("nest-cli.json"))
        assertTrue(isNestJsonFile(".nest-cli.json"))
    }

    @Test
    fun `isNestJsonFile should return false for invalid file names`() {
        assertFalse(isNestJsonFile("package.json"))
        assertFalse(isNestJsonFile("tsconfig.json"))
        assertFalse(isNestJsonFile("nest-cli.json.bak"))
        assertFalse(isNestJsonFile("nest-cli.jsonx"))
        assertFalse(isNestJsonFile(""))
        assertFalse(isNestJsonFile("nestcli.json"))
    }

    @Test
    fun `isNestJsonFile should be case sensitive`() {
        assertFalse(isNestJsonFile("NEST-CLI.JSON"))
        assertFalse(isNestJsonFile("Nest-Cli.Json"))
        assertFalse(isNestJsonFile(".NEST-CLI.JSON"))
    }

    @Test
    fun `getCliParamText should return kebab case for version 12 or higher`() {
        // Create SemVer objects for testing
        val version120 = com.intellij.util.text.SemVer.parseFromText("12.0.0")!!
        val version150 = com.intellij.util.text.SemVer.parseFromText("15.0.0")!!
        
        assertEquals("my-param", getCliParamText("myParam", version120))
        assertEquals("my-param", getCliParamText("myParam", version150))
        assertEquals("another-param", getCliParamText("anotherParam", version120))
    }

    @Test
    fun `getCliParamText should return original name for version below 12`() {
        val version110 = com.intellij.util.text.SemVer.parseFromText("11.9.9")!!
        val version100 = com.intellij.util.text.SemVer.parseFromText("10.5.3")!!
        
        assertEquals("myParam", getCliParamText("myParam", version110))
        assertEquals("anotherParam", getCliParamText("anotherParam", version100))
    }

    @Test
    fun `getCliParamText should handle version exactly 12`() {
        val version120 = com.intellij.util.text.SemVer.parseFromText("12.0.0")!!
        val version1199 = com.intellij.util.text.SemVer.parseFromText("11.99.99")!!
        
        assertEquals("my-param", getCliParamText("myParam", version120))
        assertEquals("myParam", getCliParamText("myParam", version1199))
    }
}
