package com.coconutmkii.nestjsidea.util

import com.intellij.execution.RunManager
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.javascript.JSRunConfigurationBuilder
import com.intellij.javascript.nodejs.CompletionModuleInfo
import com.intellij.javascript.nodejs.NodeModuleSearchUtil
import com.intellij.javascript.nodejs.util.NodePackage
import com.intellij.lang.javascript.JSStringUtil
import com.intellij.lang.javascript.buildTools.npm.PackageJsonUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.text.SemVer
import org.jetbrains.annotations.NonNls

const val NESTJS_CLI_PACKAGE = "@nestjs/cli"
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

fun findPackageJsonFile(rootDir: VirtualFile?): VirtualFile? {
    if (rootDir == null || !rootDir.isValid) {
        return null
    }
    for (name in NESTJS_JSON_NAMES) {
        val cliJson = rootDir.findChild(name)
        if (cliJson != null) {
            return cliJson
        }
    }
    return null
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

fun findNestJSCliFolder(project: Project, file: VirtualFile?): VirtualFile? {
    var current = file
    while (current != null) {
        if (current.isDirectory && findPackageJsonFile(current) != null) {
            return current
        }
        current = current.parent
    }
    @Suppress("DEPRECATION")
    return if (findPackageJsonFile(project.baseDir) != null) {
        project.baseDir
    }
    else null
}

fun getNestCliPackageVersion(cli: VirtualFile): SemVer? {
    val moduleInfo = findNestCliModuleInfo(cli) ?: return null

    return NodePackage(moduleInfo.virtualFile!!.path).version
}

private fun findNestCliModuleInfo(cli: VirtualFile?): CompletionModuleInfo? {
    val modules = ArrayList<CompletionModuleInfo>()
    NodeModuleSearchUtil.findModulesWithName(modules, NESTJS_CLI_PACKAGE, cli, null)
    val moduleInfo = modules.firstOrNull()
    return if (moduleInfo != null && moduleInfo.virtualFile != null) moduleInfo else null
}

private fun getPackageJson(baseDir: VirtualFile): String? {
    val pkg = PackageJsonUtil.findChildPackageJsonFile(baseDir)
    return pkg?.path
}

fun getCliParamText(name: String, cliVersion: SemVer): String {
    val toKebabCase = cliVersion.isGreaterOrEqualThan(12, 0, 0)
    val paramText = if (toKebabCase)
        JSStringUtil.toKebabCase(name, true, true, false)
    else
        name
    return "$paramText"
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

