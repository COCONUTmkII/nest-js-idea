package com.coconutmkii.nestjsidea.framework

import com.coconutmkii.nestjsidea.NestJSBundle
import com.coconutmkii.nestjsidea.NestJSIcons.nestIcon
import com.intellij.framework.FrameworkType
import javax.swing.Icon
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.NonNls

class NestJSFramework private constructor() : FrameworkType(ID) {
    override fun getPresentableName(): @Nls(capitalization = Nls.Capitalization.Title) String {
        return NestJSBundle.message("nestjs.cli")
    }

    override fun getIcon(): Icon = nestIcon

    companion object {
        @JvmField
        val INSTANCE = NestJSFramework()

        @NonNls
        const val ID = "NestjsCLI"
    }
}