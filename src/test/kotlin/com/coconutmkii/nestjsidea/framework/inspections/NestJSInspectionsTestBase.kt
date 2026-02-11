package com.coconutmkii.nestjsidea.framework.inspections

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.io.File

abstract class NestJSInspectionsTestBase : BasePlatformTestCase() {
    protected fun enable(inspections: Array<LocalInspectionTool>) = myFixture.enableInspections(*inspections)

    override fun getTestDataPath(): String = File("src/test/testData").absolutePath

    protected fun checkFile(path: String) {
        myFixture.configureByFile(path)
        myFixture.checkHighlighting()
    }

    protected fun checkDirectory(path: String, entry: String) {
        myFixture.copyDirectoryToProject(path, "")
        myFixture.configureFromTempProjectFile(entry)
        myFixture.checkHighlighting()
    }

}