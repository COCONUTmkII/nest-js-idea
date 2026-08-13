package com.coconutmkii.nestjsidea.framework.inspections

import com.coconutmkii.nestjsidea.inspections.NestJSModuleIsNotUsedProvidedInspection

class NestJSModuleInspectionTest : NestJSInspectionsTestBase() {
    override fun setUp() {
        super.setUp()
        enable(arrayOf(NestJSModuleIsNotUsedProvidedInspection()))
    }

    // Контрольный случай: обычная ссылка на модуль в imports.
    fun testPlainImportIsNotReported() {
        checkDirectory(
            "inspections/module/plainImport",
            "config.module.ts"
        )
    }

    // Проверяемый случай: тот же модуль, но подключённый через forRoot().
    fun testForRootImportIsNotReported() {
        checkDirectory(
            "inspections/module/forRoot",
            "config.module.ts"
        )
    }
}
