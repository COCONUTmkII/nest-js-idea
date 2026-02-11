package com.coconutmkii.nestjsidea.framework.inspections

import com.coconutmkii.nestjsidea.inspections.NestJSControllerIsNotProvidedInspection

class NestJSControllerInspectionTest : NestJSInspectionsTestBase() {
    override fun setUp() {
        super.setUp()
        enable(arrayOf(NestJSControllerIsNotProvidedInspection()))
    }

    fun testHighlightsControllerWhenNotProvidedInAnyModule() {
        checkFile("inspections/controller/notProvided.ts")
    }

    fun testDoesNotHighlightWhenControllerIsProvidedInModule() {
        checkFile("inspections/controller/provided.ts")
    }

    fun testIgnoreAbstractControllerFromHighlight() {
        checkFile("inspections/controller/abstract.ts")
    }

    fun testIgnoreDecoratorFromWrongImport() {
        checkFile("inspections/controller/wrongImport.ts")
    }

    fun testWorksAcrossMultipleFiles() {
        checkDirectory(
            "inspections/controller/multiModule",
            "my.controller.ts"
        )
    }

}
