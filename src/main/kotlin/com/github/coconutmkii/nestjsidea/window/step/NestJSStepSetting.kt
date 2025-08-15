package com.github.coconutmkii.nestjsidea.window.step

import com.github.coconutmkii.nestjsidea.framework.manager.PackageManager
import com.intellij.lang.javascript.boilerplate.NpmPackageProjectGenerator.Settings

data class NestJSStepSetting(
    val settings: Settings,
    val packageManager: PackageManager
) : Settings(settings.myInterpreterRef, settings.myPackage)


