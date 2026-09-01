package com.coconutmkii.nestjsidea.framework.file.validator

import com.coconutmkii.nestjsidea.NestJSBundle
import com.intellij.openapi.ui.InputValidatorEx
import com.intellij.openapi.util.NlsContexts

class NoWhitespaceValidator : InputValidatorEx {
    private val whitespace = "\\s".toRegex()
    override fun checkInput(inputString: String?): Boolean {
        return !inputString.isNullOrBlank() && !inputString.contains(whitespace)
    }

    override fun getErrorText(inputString: String?): @NlsContexts.DetailedDescription String? =
        if (checkInput(inputString)) null
        else NestJSBundle.message("nestjs.dialog.title.new.nest.file.validation.error")

    override fun canClose(inputString: String?): Boolean = checkInput(inputString)
}
