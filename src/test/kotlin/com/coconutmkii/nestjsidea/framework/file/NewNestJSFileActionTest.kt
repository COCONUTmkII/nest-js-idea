package com.coconutmkii.nestjsidea.framework.file

import org.junit.Assert.*
import org.junit.Test

class NewNestJSFileActionTest {

    @Test
    fun `template constants should be defined correctly`() {
        assertEquals("NestJS Controller", NestJSFileTemplate.CONTROLLER.templateName)
        assertEquals("NestJS Service", NestJSFileTemplate.SERVICE.templateName)
        assertEquals("NestJS Module", NestJSFileTemplate.MODULE.templateName)
        assertEquals("NestJS Pipe", NestJSFileTemplate.PIPE.templateName)
        assertEquals("NestJS Guard", NestJSFileTemplate.GUARD.templateName)
        assertEquals("NestJS Resolver", NestJSFileTemplate.RESOLVER.templateName)
    }

    @Test
    fun `every template is resolvable by its name`() {
        NestJSFileTemplate.entries.forEach {
            assertEquals(it, NestJSFileTemplate.byTemplateName(it.templateName))
        }
        assertNull(NestJSFileTemplate.byTemplateName("Unknown"))
    }
}
