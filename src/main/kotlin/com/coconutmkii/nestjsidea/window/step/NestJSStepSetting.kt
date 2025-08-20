package com.coconutmkii.nestjsidea.window.step

import com.coconutmkii.nestjsidea.framework.manager.PackageManager
import com.intellij.lang.javascript.boilerplate.NpmPackageProjectGenerator.Settings

class NestJSStepSetting(
    original: Settings,
    val packageManager: PackageManager
) : Settings(
    original.myInterpreterRef,
    original.myPackage
)


