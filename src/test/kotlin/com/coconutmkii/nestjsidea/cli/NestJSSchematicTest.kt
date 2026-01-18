package com.coconutmkii.nestjsidea.cli

import org.junit.Assert.*
import org.junit.Test

class NestJSSchematicTest {

    @Test
    fun `NestJSSchematic should create with all properties`() {
        val schematic = NestJSSchematic(
            name = "controller",
            description = "Generate a controller",
            options = listOf("--flat", "--spec"),
            arguments = listOf("name")
        )

        assertEquals("controller", schematic.name)
        assertEquals("Generate a controller", schematic.description)
        assertEquals(listOf("--flat", "--spec"), schematic.options)
        assertEquals(listOf("name"), schematic.arguments)
    }

    @Test
    fun `NestJSSchematic should support empty options and arguments`() {
        val schematic = NestJSSchematic(
            name = "class",
            description = "Generate a class",
            options = emptyList(),
            arguments = emptyList()
        )

        assertTrue(schematic.options.isEmpty())
        assertTrue(schematic.arguments.isEmpty())
    }

    @Test
    fun `NestJSSchematic should be a data class with proper equality`() {
        val schematic1 = NestJSSchematic(
            name = "service",
            description = "Generate a service",
            options = listOf("--flat"),
            arguments = listOf()
        )
        val schematic2 = NestJSSchematic(
            name = "service",
            description = "Generate a service",
            options = listOf("--flat"),
            arguments = listOf()
        )
        val schematic3 = NestJSSchematic(
            name = "service",
            description = "Different description",
            options = listOf("--flat"),
            arguments = listOf()
        )

        assertEquals(schematic1, schematic2)
        assertNotEquals(schematic1, schematic3)
    }
}
