package com.github.coconutmkii.nestjsidea.framework.file

import com.github.coconutmkii.nestjsidea.NestJSPluginBundle
import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.lang.javascript.TypeScriptFileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.NlsContexts
import com.intellij.psi.PsiDirectory
import org.jetbrains.annotations.NonNls

class NewNestJsFileAction : CreateFileFromTemplateAction(
    "NestJS File",
    "Create a new NestJS file",
    IconLoader.getIcon("/icons/nestjsIcon.png", NewNestJsFileAction::class.java),
) {
    override fun buildDialog(
        project: Project,
        directory: PsiDirectory,
        builder: CreateFileFromTemplateDialog.Builder
    ) {
        builder.setTitle(NestJSPluginBundle.message("nestjs.dialog.title.new.nest.file"))
            .addKind(NestJSPluginBundle.message("nestjs.dialog.title.new.nest.file.controller"), TypeScriptFileType.INSTANCE.icon, "My Class")
            .addKind(NestJSPluginBundle.message("nestjs.dialog.title.new.nest.file.service"), TypeScriptFileType.INSTANCE.icon, "My Class")
            .addKind(NestJSPluginBundle.message("nestjs.dialog.title.new.nest.file.module"), TypeScriptFileType.INSTANCE.icon, "My Class")
            .addKind(NestJSPluginBundle.message("nestjs.dialog.title.new.nest.file.middleware"), TypeScriptFileType.INSTANCE.icon, "My Class")
            .addKind(NestJSPluginBundle.message("nestjs.dialog.title.new.nest.file.pipe"), TypeScriptFileType.INSTANCE.icon, "My Class")
            .addKind(NestJSPluginBundle.message("nestjs.dialog.title.new.nest.file.guard"), TypeScriptFileType.INSTANCE.icon, "My Class")
    }

    override fun getActionName(
        directory: PsiDirectory?,
        newName: @NonNls String,
        templateName: @NonNls String?
    ): @NlsContexts.Command String? {
        return NestJSPluginBundle.message("nestjs.dialog.title.new.nest.file", arrayOf(newName))
    }

}
