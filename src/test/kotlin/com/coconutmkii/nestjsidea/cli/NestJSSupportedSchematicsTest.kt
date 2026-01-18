package com.coconutmkii.nestjsidea.cli

import org.junit.Assert.*
import org.junit.Test

class NestJSSupportedSchematicsTest {

    @Test
    fun `supportedSchematics should contain all expected schematics`() {
        val schematicNames = NestJSSupportedSchematics.supportedSchematics.map { it.name }

        assertTrue(schematicNames.contains("app"))
        assertTrue(schematicNames.contains("library"))
        assertTrue(schematicNames.contains("class"))
        assertTrue(schematicNames.contains("controller"))
        assertTrue(schematicNames.contains("service"))
        assertTrue(schematicNames.contains("module"))
        assertTrue(schematicNames.contains("guard"))
        assertTrue(schematicNames.contains("pipe"))
        assertTrue(schematicNames.contains("decorator"))
        assertTrue(schematicNames.contains("filter"))
        assertTrue(schematicNames.contains("gateway"))
        assertTrue(schematicNames.contains("interface"))
        assertTrue(schematicNames.contains("interceptor"))
        assertTrue(schematicNames.contains("middleware"))
        assertTrue(schematicNames.contains("provider"))
        assertTrue(schematicNames.contains("resolver"))
    }

    @Test
    fun `supportedSchematics should have unique names`() {
        val names = NestJSSupportedSchematics.supportedSchematics.map { it.name }
        val uniqueNames = names.toSet()

        assertEquals(names.size, uniqueNames.size)
    }

    @Test
    fun `supportedSchematics should have non-empty descriptions`() {
        NestJSSupportedSchematics.supportedSchematics.forEach { schematic ->
            assertTrue("Schematic '${schematic.name}' should have a description", schematic.description.isNotBlank())
        }
    }

    @Test
    fun `supportedSchematics should all have options`() {
        NestJSSupportedSchematics.supportedSchematics.forEach { schematic ->
            assertTrue("Schematic '${schematic.name}' should have options", schematic.options.isNotEmpty())
        }
    }

    @Test
    fun `supportedGenerateSchematicOptions should contain all expected options`() {
        val options = NestJSSupportedSchematics.supportedGenerateSchematicOptions

        assertTrue(options.contains("--dry-run"))
        assertTrue(options.contains("--project"))
        assertTrue(options.contains("--flat"))
        assertTrue(options.contains("--collection"))
        assertTrue(options.contains("--spec"))
        assertTrue(options.contains("--no-spec"))
    }

    @Test
    fun `schematicOptionDescriptions should have descriptions for all supported options`() {
        NestJSSupportedSchematics.supportedGenerateSchematicOptions.forEach { option ->
            assertTrue(
                "Option '$option' should have a description",
                NestJSSupportedSchematics.schematicOptionDescriptions.containsKey(option)
            )
            assertTrue(
                "Option '$option' description should not be blank",
                NestJSSupportedSchematics.schematicOptionDescriptions[option]?.isNotBlank() == true
            )
        }
    }

    @Test
    fun `all schematics should use supportedGenerateSchematicOptions`() {
        val supportedOptions = NestJSSupportedSchematics.supportedGenerateSchematicOptions.toSet()

        NestJSSupportedSchematics.supportedSchematics.forEach { schematic ->
            schematic.options.forEach { option ->
                assertTrue(
                    "Schematic '${schematic.name}' uses unsupported option '$option'",
                    supportedOptions.contains(option)
                )
            }
        }
    }
}
