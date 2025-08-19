package com.github.coconutmkii.nestjsidea.framework.file

import com.github.coconutmkii.nestjsidea.NestJSPluginBundle
import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.NlsContexts
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import org.jetbrains.annotations.NonNls

const val CONTROLLER_TEMPLATE = "NestJS Controller"
const val SERVICE_TEMPLATE = "NestJS Service"
const val MODULE_TEMPLATE = "NestJS Module"
const val PIPE_TEMPLATE = "NestJS Pipe"
const val GUARD_TEMPLATE = "NestJS Guard"

class NewNestJsFileAction : CreateFileFromTemplateAction(
    "NestJS File",
    "Create a new NestJS file",
    IconLoader.getIcon("/icons/nestjsIcon.png", NewNestJsFileAction::class.java),
) {
    private val controllerIcon = IconLoader.getIcon("/icons/controllerIcon.svg", NewNestJsFileAction::class.java)
    private val serviceIcon = IconLoader.getIcon("/icons/serviceIcon.svg", NewNestJsFileAction::class.java)
    private val moduleIcon = IconLoader.getIcon("/icons/moduleIcon.svg", NewNestJsFileAction::class.java)
    private val pipeIcon = IconLoader.getIcon("/icons/pipeIcon.svg", NewNestJsFileAction::class.java)
    private val guardIcon = IconLoader.getIcon("/icons/guardIcon.svg", NewNestJsFileAction::class.java)
    override fun buildDialog(
        project: Project,
        directory: PsiDirectory,
        builder: CreateFileFromTemplateDialog.Builder
    ) {
        builder.setTitle(NestJSPluginBundle.message("nestjs.dialog.title.new.nest.file"))
            .addKind(NestJSPluginBundle.message("nestjs.dialog.title.new.nest.file.controller"), controllerIcon, CONTROLLER_TEMPLATE)
            .addKind(NestJSPluginBundle.message("nestjs.dialog.title.new.nest.file.service"), serviceIcon, SERVICE_TEMPLATE)
            .addKind(NestJSPluginBundle.message("nestjs.dialog.title.new.nest.file.module"), moduleIcon, MODULE_TEMPLATE)
            .addKind(NestJSPluginBundle.message("nestjs.dialog.title.new.nest.file.pipe"), pipeIcon, PIPE_TEMPLATE)
            .addKind(NestJSPluginBundle.message("nestjs.dialog.title.new.nest.file.guard"), guardIcon, GUARD_TEMPLATE)
    }

    override fun getActionName(
        directory: PsiDirectory?,
        newName: @NonNls String,
        templateName: @NonNls String?
    ): @NlsContexts.Command String? {
        return NestJSPluginBundle.message("nestjs.dialog.title.new.nest.file", arrayOf(newName))
    }

    override fun createFileFromTemplate(name: String, template: FileTemplate, dir: PsiDirectory): PsiFile? {
        val extension = ".ts"
        val baseName = name.removeSuffix("Controller")
            .removeSuffix("Service")
            .removeSuffix("Module")
            .removeSuffix("Pipe")
            .removeSuffix("Guard")

        val fileName = when (template.name) {
            CONTROLLER_TEMPLATE -> "${toKebabCase(baseName)}.controller$extension"
            SERVICE_TEMPLATE -> "${toKebabCase(baseName)}.service$extension"
            MODULE_TEMPLATE -> "${toKebabCase(baseName)}.module$extension"
            PIPE_TEMPLATE -> "${toKebabCase(baseName)}.pipe$extension"
            GUARD_TEMPLATE -> "${toKebabCase(baseName)}.guard$extension"
            else -> "$name$extension"
        }

        val props = FileTemplateManager.getInstance(dir.project).defaultProperties
        props["NAME"] = name

        return FileTemplateUtil.createFromTemplate(template, fileName, props, dir) as PsiFile
    }

    private fun toKebabCase(input: String): String = input
        .replace(Regex("([a-z])([A-Z])"), "$1-$2")
        .lowercase()

}
