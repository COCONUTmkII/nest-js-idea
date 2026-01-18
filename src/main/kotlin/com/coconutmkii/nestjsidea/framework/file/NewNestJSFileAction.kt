package com.coconutmkii.nestjsidea.framework.file

import com.coconutmkii.nestjsidea.NestJSBundle
import com.coconutmkii.nestjsidea.NestJSIcons.controllerIcon
import com.coconutmkii.nestjsidea.NestJSIcons.guardIcon
import com.coconutmkii.nestjsidea.NestJSIcons.moduleIcon
import com.coconutmkii.nestjsidea.NestJSIcons.pipeIcon
import com.coconutmkii.nestjsidea.NestJSIcons.serviceIcon
import com.coconutmkii.nestjsidea.framework.file.validator.NoWhitespaceValidator
import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.fileTemplates.FileTemplate
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.ide.fileTemplates.FileTemplateUtil
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.vfs.LocalFileSystem
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

    override fun isAvailable(dataContext: DataContext): Boolean {
        val project: Project = CommonDataKeys.PROJECT.getData(dataContext) ?: return false
        val dir: PsiDirectory? = CommonDataKeys.PSI_ELEMENT.getData(dataContext) as? PsiDirectory
            ?: CommonDataKeys.VIRTUAL_FILE.getData(dataContext)?.let { vf ->
                com.intellij.psi.PsiManager.getInstance(project).findDirectory(vf)
            }

        if (dir == null) return false

        return isNestProject(dir)
    }

    override fun buildDialog(
        project: Project,
        directory: PsiDirectory,
        builder: CreateFileFromTemplateDialog.Builder
    ) {
        builder.setTitle(NestJSBundle.message("nestjs.dialog.title.new.nest.file"))
            .addKind(NestJSBundle.message("nestjs.dialog.title.new.nest.file.controller"), controllerIcon, CONTROLLER_TEMPLATE)
            .addKind(NestJSBundle.message("nestjs.dialog.title.new.nest.file.service"), serviceIcon, SERVICE_TEMPLATE)
            .addKind(NestJSBundle.message("nestjs.dialog.title.new.nest.file.module"), moduleIcon, MODULE_TEMPLATE)
            .addKind(NestJSBundle.message("nestjs.dialog.title.new.nest.file.pipe"), pipeIcon, PIPE_TEMPLATE)
            .addKind(NestJSBundle.message("nestjs.dialog.title.new.nest.file.guard"), guardIcon, GUARD_TEMPLATE)
            .setValidator(NoWhitespaceValidator())
    }

    override fun getActionName(
        directory: PsiDirectory?,
        newName: @NonNls String,
        templateName: @NonNls String?
    ): @NlsContexts.Command String? {
        return NestJSBundle.message("nestjs.dialog.title.new.nest.file", arrayOf(newName))
    }

    override fun createFileFromTemplate(name: String, template: FileTemplate, dir: PsiDirectory): PsiFile? {
        val extension = ".ts"

        val cleanName = name
            .trim()
            .replace("\\s+".toRegex(), "")

        val baseName = cleanName.removeSuffix("Controller")
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

    private fun isNestProject(directory: PsiDirectory): Boolean {
        val basePath = directory.project.basePath ?: return false
        val packageJson = LocalFileSystem.getInstance().findFileByPath(basePath)?.findChild("package.json") ?: return false
        val text = String(packageJson.contentsToByteArray())
        return text.contains("@nestjs/core")
    }

}
