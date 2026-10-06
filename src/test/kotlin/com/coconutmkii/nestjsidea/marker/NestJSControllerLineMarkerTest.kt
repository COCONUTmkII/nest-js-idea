package com.coconutmkii.nestjsidea.marker

import com.coconutmkii.nestjsidea.NestJSBundle
import com.coconutmkii.nestjsidea.framework.inspections.NestJSInspectionsTestBase

class NestJSControllerLineMarkerTest : NestJSInspectionsTestBase() {
    private val tooltip
        get() = NestJSBundle.message("nestjs.marker.controller.navigate.to.modules")

    fun testMarkerOnRegisteredControllerClassName() {
        myFixture.copyDirectoryToProject("marker/controller/provided", "")
        myFixture.configureFromTempProjectFile("app.controller.ts")
        myFixture.doHighlighting()

        moveCaretTo("export class AppController")
        assertTrue(myFixture.findGuttersAtCaret().any { it.tooltipText == tooltip })

        moveCaretTo("@Controller()")
        assertFalse(myFixture.findGuttersAtCaret().any { it.tooltipText == tooltip })

        moveCaretTo("@Get()")
        assertFalse(myFixture.findGuttersAtCaret().any { it.tooltipText == tooltip })
    }

    fun testNoMarkerOnInjectable() {
        myFixture.copyDirectoryToProject("marker/controller/provided", "")
        myFixture.configureFromTempProjectFile("app.service.ts")
        myFixture.doHighlighting()

        assertFalse(myFixture.findAllGutters().any { it.tooltipText == tooltip })
    }

    fun testNoMarkerWhenControllerIsNotRegistered() {
        myFixture.configureByFile("marker/controller/notProvided.ts")
        myFixture.doHighlighting()

        assertFalse(myFixture.findAllGutters().any { it.tooltipText == tooltip })
    }

    private fun moveCaretTo(snippet: String) {
        val offset = myFixture.file.text.indexOf(snippet)
        assertTrue("'$snippet' not found", offset >= 0)
        myFixture.editor.caretModel.moveToOffset(offset)
    }
}
