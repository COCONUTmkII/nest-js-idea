package com.coconutmkii.nestjsidea.framework.file

import com.intellij.codeInsight.folding.CodeFoldingSettings
import com.intellij.openapi.startup.StartupActivity
import com.intellij.openapi.project.Project

class NestJSFoldingDefaultsSetter : StartupActivity {
    override fun runActivity(project: Project) {
        CodeFoldingSettings.getInstance()?.takeIf { !it.COLLAPSE_IMPORTS }?.let { it.COLLAPSE_IMPORTS = true }
    }
}
