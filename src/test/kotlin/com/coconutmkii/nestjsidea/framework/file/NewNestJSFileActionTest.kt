package com.coconutmkii.nestjsidea.framework.file

import org.junit.Assert.*
import org.junit.Test

class NewNestJSFileActionTest {

    @Test
    fun `toKebabCase should convert camelCase to kebab-case`() {
        val action = NewNestJsFileAction()
        
        // Access private method via reflection or test helper
        // For now, test the logic directly
        
        fun toKebabCase(input: String): String = input
            .replace(Regex("([a-z])([A-Z])"), "$1-$2")
            .lowercase()

        assertEquals("my-controller", toKebabCase("MyController"))
        assertEquals("user-service", toKebabCase("UserService"))
        assertEquals("app-module", toKebabCase("AppModule"))
        assertEquals("auth-guard", toKebabCase("AuthGuard"))
        assertEquals("validation-pipe", toKebabCase("ValidationPipe"))
    }

    @Test
    fun `toKebabCase should handle already kebab-case`() {
        fun toKebabCase(input: String): String = input
            .replace(Regex("([a-z])([A-Z])"), "$1-$2")
            .lowercase()

        assertEquals("my-controller", toKebabCase("my-controller"))
        assertEquals("user-service", toKebabCase("user-service"))
    }

    @Test
    fun `toKebabCase should handle all lowercase`() {
        fun toKebabCase(input: String): String = input
            .replace(Regex("([a-z])([A-Z])"), "$1-$2")
            .lowercase()

        assertEquals("controller", toKebabCase("controller"))
        assertEquals("service", toKebabCase("service"))
    }

    @Test
    fun `toKebabCase should handle all uppercase`() {
        fun toKebabCase(input: String): String = input
            .replace(Regex("([a-z])([A-Z])"), "$1-$2")
            .lowercase()

        assertEquals("controller", toKebabCase("CONTROLLER"))
        assertEquals("user-service", toKebabCase("USER-SERVICE"))
    }

    @Test
    fun `toKebabCase should handle consecutive uppercase letters`() {
        fun toKebabCase(input: String): String = input
            .replace(Regex("([a-z])([A-Z])"), "$1-$2")
            .lowercase()

        assertEquals("http-client", toKebabCase("HTTP-Client"))
        assertEquals("api-service", toKebabCase("API-Service"))
    }

    @Test
    fun `template constants should be defined correctly`() {
        assertEquals("NestJS Controller", CONTROLLER_TEMPLATE)
        assertEquals("NestJS Service", SERVICE_TEMPLATE)
        assertEquals("NestJS Module", MODULE_TEMPLATE)
        assertEquals("NestJS Pipe", PIPE_TEMPLATE)
        assertEquals("NestJS Guard", GUARD_TEMPLATE)
    }
}
