package com.github.coconutmkii.nestjsidea.window.step

import com.intellij.lang.javascript.boilerplate.NpmPackageProjectGenerator.Settings

class NestJSStepSetting(
    original: Settings
) : Settings(
    original.myInterpreterRef,
    original.myPackage
)


