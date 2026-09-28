package com.coconutmkii.nestjsidea.util

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class IsNestProjectTest : BasePlatformTestCase() {

    fun `test project without package json is not a nest project`() {
        assertFalse(isNestProject(project))
    }

    fun `test package json with nest core dependency is detected`() {
        myFixture.addFileToProject(
            "package.json",
            """
            {
              "name": "demo",
              "dependencies": {
                "@nestjs/core": "^10.0.0"
              }
            }
            """.trimIndent()
        )

        assertTrue(isNestProject(project))
    }

    fun `test package json without nest core dependency is not a nest project`() {
        myFixture.addFileToProject(
            "package.json",
            """
            {
              "name": "demo",
              "dependencies": {
                "express": "^4.19.2"
              }
            }
            """.trimIndent()
        )

        assertFalse(isNestProject(project))
    }

    fun `test cached answer is recalculated after package json appears`() {
        assertFalse(isNestProject(project))

        myFixture.addFileToProject(
            "package.json",
            """
            {
              "name": "demo",
              "dependencies": {
                "@nestjs/core": "^10.0.0"
              }
            }
            """.trimIndent()
        )

        assertTrue(isNestProject(project))
    }

    // The old implementation matched "@nestjs/core" anywhere in the file, including scripts and comments.
    fun `test nest core mentioned outside dependencies is not a nest project`() {
        myFixture.addFileToProject(
            "package.json",
            """
            {
              "name": "demo",
              "description": "migrating away from @nestjs/core",
              "scripts": {
                "why": "echo @nestjs/core"
              }
            }
            """.trimIndent()
        )

        assertFalse(isNestProject(project))
    }
}
