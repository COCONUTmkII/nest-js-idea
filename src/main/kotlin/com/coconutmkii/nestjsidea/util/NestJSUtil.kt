package com.coconutmkii.nestjsidea.util

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.javascript.JSRunConfigurationBuilder
import com.intellij.lang.javascript.buildTools.npm.PackageJsonUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.vfs.VirtualFile
import org.jetbrains.annotations.NonNls

private val NESTJS_JSON_NAMES = listOf("nest-cli.json", ".nest-cli.json")
private val NEST_JS_EXCLUDES = listOf("dist", "out", "tmp", "coverage", "build")

fun isNestJsonFile(fileName: String): Boolean = NESTJS_JSON_NAMES.contains(fileName)

fun ContentEntry.addDefaultNestExcludes(rootDir: VirtualFile) {
    for (name in NEST_JS_EXCLUDES) {
        val dir = rootDir.findChild(name)
        if (dir != null && dir.isDirectory) {
            this.addExcludeFolder(dir)
        }
    }
}

fun createNestRunConfigurations(project: Project, baseDir: VirtualFile) {
    ApplicationManager.getApplication().executeOnPooledThread {
        DumbService.getInstance(project).runReadActionInSmartMode {
            if (project.isDisposed) {
                return@runReadActionInSmartMode
            }

            val packageJsonPath = getPackageJson(baseDir)
                ?: return@runReadActionInSmartMode

            val nameSuffix = if (ModuleManager.getInstance(project).modules.size > 1)
                " (" + baseDir.name + ")"
            else
                ""

            RunManager.getInstance(project).selectedConfiguration = createNpmConfiguration(
                project, packageJsonPath, "Nest CLI Server$nameSuffix"
            )
        }
    }
}

private fun getPackageJson(baseDir: VirtualFile): String? {
    val pkg = PackageJsonUtil.findChildPackageJsonFile(baseDir)
    return pkg?.path
}

private fun createNpmConfiguration(
    project: Project,
    packageJsonPath: String,
    @NonNls label: String
): RunnerAndConfigurationSettings? {
    return createIfNoSimilar(
        project, label, null, packageJsonPath,
        mapOf("run-script" to "start")
    )
}

private fun createIfNoSimilar(
    project: Project,
    @NonNls label: String,
    baseDir: VirtualFile?,
    configPath: String?,
    options: Map<String, Any>
): RunnerAndConfigurationSettings? {
    return JSRunConfigurationBuilder.getForName("npm", project)?.let { builder ->
        builder.findSimilarRunConfiguration(baseDir, configPath, options)
            ?: builder.createRunConfiguration(label, baseDir, configPath, options)
    }
}

