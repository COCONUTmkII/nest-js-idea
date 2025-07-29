package com.github.coconutmkii.nestjsidea.framework

import com.github.coconutmkii.nestjsidea.NestJSPluginBundle
import com.intellij.framework.FrameworkType
import javax.swing.Icon
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls

class NestJSFramework private constructor() : FrameworkType(ID) {
    override fun getPresentableName(): @Nls(capitalization = Nls.Capitalization.Title) String {
        return NestJSPluginBundle.message("nestjs.cli")
    }

    override fun getIcon(): Icon = icons.CollaborationToolsIcons.Send

    companion object {
        @JvmField
        val INSTANCE = NestJSFramework()

        @NonNls
        const val ID = "NestjsCLI"
    }
}