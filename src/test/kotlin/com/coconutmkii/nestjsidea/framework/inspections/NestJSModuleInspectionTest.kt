package com.coconutmkii.nestjsidea.framework.inspections

import com.coconutmkii.nestjsidea.inspections.NestJSModuleIsNotUsedProvidedInspection

class NestJSModuleInspectionTest : NestJSInspectionsTestBase() {
    override fun setUp() {
        super.setUp()
        enable(arrayOf(NestJSModuleIsNotUsedProvidedInspection()))
    }

    fun `test plain import is not reported`() {
        checkDirectory(
            "inspections/module/plainImport",
            "config.module.ts"
        )
    }

    // The same module but with forRoot()
    fun `test for root import is not reported`() {
        checkDirectory(
            "inspections/module/forRoot",
            "config.module.ts"
        )
    }
}
