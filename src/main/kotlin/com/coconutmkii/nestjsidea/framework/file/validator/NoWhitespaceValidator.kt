package com.coconutmkii.nestjsidea.framework.file.validator

import com.coconutmkii.nestjsidea.NestJSBundle
import com.intellij.openapi.ui.InputValidatorEx
import com.intellij.openapi.util.NlsContexts
import org.jetbrains.annotations.NonNls

class NoWhitespaceValidator : InputValidatorEx {
    override fun getErrorText(inputString: @NonNls String?): @NlsContexts.DetailedDescription String? {
        return NestJSBundle.message("nestjs.dialog.title.new.nest.file.validation.error")
    }

    override fun checkInput(inputString: String?): Boolean {
        if (inputString.isNullOrBlank()) return false
        return !inputString.contains("\\s".toRegex())
    }

    override fun canClose(inputString: String?): Boolean = checkInput(inputString)
}
