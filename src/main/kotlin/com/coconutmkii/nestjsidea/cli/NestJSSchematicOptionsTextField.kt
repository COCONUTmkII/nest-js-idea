package com.coconutmkii.nestjsidea.cli

import com.coconutmkii.nestjsidea.cli.NestJSSupportedSchematics.schematicOptionDescriptions
import com.coconutmkii.nestjsidea.util.getCliParamText
import com.intellij.codeInsight.lookup.CharFilter
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.TextFieldWithAutoCompletion
import com.intellij.ui.TextFieldWithAutoCompletionListProvider
import com.intellij.util.text.SemVer

class NestJSSchematicOptionsTextField(
    project: Project,
    options: List<String>, // может поменять на что-то конкретное типа интерфейса
    cliVersion: SemVer
) : TextFieldWithAutoCompletion<String>(project, NestJSSchematicOptionsCompletionProvider(options, cliVersion), false, null) {
    private class NestJSSchematicOptionsCompletionProvider(
        options: List<String>,
        private val cliVersion: SemVer
    ) : TextFieldWithAutoCompletionListProvider<String>(options) {
        override fun getLookupString(item: String): String {
            return getCliParamText(item, cliVersion) // return item.name?.let { AngularCliUtil.getCliParamText(it, cliVersion) } ?: ""
        }

        override fun acceptChar(c: Char): CharFilter.Result? {
            return if (c == '-') CharFilter.Result.ADD_TO_PREFIX else null
        }

        override fun compare(item1: String?, item2: String?): Int {
            return StringUtil.compare(item1, item2, false)
        }

        override fun createLookupBuilder(item: String): LookupElementBuilder {
            val optionDescription = schematicOptionDescriptions[item]
            return super.createLookupBuilder(item)
                .withTailText("  $optionDescription", true)
        }
    }
}