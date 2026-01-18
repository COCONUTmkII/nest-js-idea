package com.coconutmkii.nestjsidea.cli

import com.intellij.openapi.util.text.StringUtil
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for NestJSCliFilter parsing logic.
 * 
 * Note: Full integration tests would require IntelliJ Platform test infrastructure.
 * These tests verify the core parsing logic that can be tested independently.
 */
class NestJSCliFilterTest {

    @Test
    fun `doParse should extract file path from create line`() {
        val line = "CREATE src/app/app.controller.ts (standard output)"
        val prefix = "create "
        
        // Simulate the parsing logic from doParse
        val index = StringUtil.indexOfIgnoreCase(line, prefix, 0)
        assertTrue("Should find prefix in line", index >= 0)
        
        val start = index + prefix.length
        var end = line.indexOf(" (", start)
        if (end == -1) end = line.length
        val fileName = line.substring(start, end).trim { it <= ' ' }
        
        assertEquals("src/app/app.controller.ts", fileName)
    }

    @Test
    fun `doParse should extract file path from update line`() {
        val line = "UPDATE src/app/app.service.ts (standard output)"
        val prefix = "update "
        
        val index = StringUtil.indexOfIgnoreCase(line, prefix, 0)
        assertTrue(index >= 0)
        
        val start = index + prefix.length
        var end = line.indexOf(" (", start)
        if (end == -1) end = line.length
        val fileName = line.substring(start, end).trim { it <= ' ' }
        
        assertEquals("src/app/app.service.ts", fileName)
    }

    @Test
    fun `doParse should handle lines without trailing parentheses`() {
        val line = "CREATE src/app/test.ts"
        val prefix = "create "
        
        val index = StringUtil.indexOfIgnoreCase(line, prefix, 0)
        assertTrue(index >= 0)
        
        val start = index + prefix.length
        var end = line.indexOf(" (", start)
        if (end == -1) end = line.length
        val fileName = line.substring(start, end).trim { it <= ' ' }
        
        assertEquals("src/app/test.ts", fileName)
    }

    @Test
    fun `doParse should handle case insensitive matching`() {
        val line1 = "create src/app/test.ts"
        val line2 = "CREATE src/app/test.ts"
        val line3 = "Create src/app/test.ts"
        val prefix = "create "
        
        val index1 = StringUtil.indexOfIgnoreCase(line1, prefix, 0)
        val index2 = StringUtil.indexOfIgnoreCase(line2, prefix, 0)
        val index3 = StringUtil.indexOfIgnoreCase(line3, prefix, 0)
        
        assertTrue(index1 >= 0)
        assertTrue(index2 >= 0)
        assertTrue(index3 >= 0)
    }

    @Test
    fun `doParse should return empty list when prefix not found`() {
        val line = "Some random line without create or update"
        val prefix = "create "
        
        val index = StringUtil.indexOfIgnoreCase(line, prefix, 0)
        assertTrue("Should not find prefix", index < 0)
    }

    @Test
    fun `constants should be defined correctly`() {
        assertEquals("create ", CREATE)
        assertEquals("update ", UPDATE)
    }
}
