package com.github.coconutmkii.nestjsidea.framework

import com.github.coconutmkii.nestjsidea.NestJSPluginBundle
import com.intellij.framework.FrameworkType
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls

class NestJSFramework private constructor() : FrameworkType(ID) {
    override fun getPresentableName(): @Nls(capitalization = Nls.Capitalization.Title) String {
        return NestJSPluginBundle.message("nestjs.cli")
    }

    override fun getIcon(): Icon = IconLoader.getIcon("/icons/nestjsIcon.png", javaClass)

    companion object {
        @JvmField
        val INSTANCE = NestJSFramework()

        @NonNls
        const val ID = "NestjsCLI"
    }
}