package com.coconutmkii.nestjsidea.framework

import com.coconutmkii.nestjsidea.NestJSBundle
import com.coconutmkii.nestjsidea.util.addDefaultNestExcludes
import com.coconutmkii.nestjsidea.util.createNestRunConfigurations
import com.coconutmkii.nestjsidea.util.isNestJsonFile
import com.intellij.ide.projectView.actions.MarkRootActionBase
import com.intellij.framework.FrameworkType
import com.intellij.framework.detection.DetectedFrameworkDescription
import com.intellij.framework.detection.FileContentPattern
import com.intellij.framework.detection.FrameworkDetectionContext
import com.intellij.framework.detection.FrameworkDetector
import com.intellij.json.JsonFileType
import com.intellij.lang.javascript.library.JSLibraryUtil
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.module.ModuleUtilCore
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModifiableModelsProvider
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.roots.ui.configuration.ModulesProvider
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.patterns.ElementPattern
import com.intellij.patterns.PatternCondition
import com.intellij.patterns.StandardPatterns
import com.intellij.util.ProcessingContext
import com.intellij.util.indexing.FileContent
import com.intellij.openapi.util.registry.Registry

class NestJSFrameworkDetector : FrameworkDetector(NestJSFramework.ID) {
    override fun getFileType(): FileType = JsonFileType.INSTANCE

    override fun createSuitableFilePattern(): ElementPattern<FileContent> {
        return FileContentPattern.fileContent().withName(
            StandardPatterns.string().with(object : PatternCondition<String>("cli-json-name") {
                override fun accepts(s: String, context: ProcessingContext): Boolean {
                    return isNestJsonFile(s)
                }
            })
        ).with(object : PatternCondition<FileContent>("notLibrary") {
            override fun accepts(content: FileContent, context: ProcessingContext): Boolean {
                return !JSLibraryUtil.isProbableLibraryFile(content.file)
            }
        })
    }

    override fun detect(
        newFiles: Collection<VirtualFile>,
        context: FrameworkDetectionContext
    ): List<DetectedFrameworkDescription?>? {
        if (!Registry.`is`("nestjs.detect.cli.configuration")) return emptyList()

        return if (newFiles.isNotEmpty() && !isConfigured(newFiles, context.project)) {
            listOf(NestJSCLIFrameworkDescription(newFiles))
        }
        else emptyList()
    }

    private fun isConfigured(files: Collection<VirtualFile>, project: Project?): Boolean {
        if (project == null) return false

        for (file in files) {
            val module = ModuleUtilCore.findModuleForFile(file, project)
            if (module != null) {
                for (root in ModuleRootManager.getInstance(module).excludeRootUrls) {
                    if (root == file.parent.url + "/dist") {
                        return true
                    }
                }
            }
        }
        return false
    }

    override fun getFrameworkType(): FrameworkType = NestJSFramework.INSTANCE

    private inner class NestJSCLIFrameworkDescription(private val files: Collection<VirtualFile>) : DetectedFrameworkDescription() {
        override fun getRelatedFiles(): Collection<VirtualFile?> = files

        override fun getSetupText(): @NlsContexts.Label String = NestJSBundle.message("nestjs.cli")

        override fun getDetector(): FrameworkDetector = this@NestJSFrameworkDetector

        override fun setupFramework(
            modifiableModelsProvider: ModifiableModelsProvider,
            modulesProvider: ModulesProvider
        ) {
            for (module in modulesProvider.modules) {
                val model = modifiableModelsProvider.getModuleModifiableModel(module)
                val item = files.firstOrNull()
                val entry = if (item != null) MarkRootActionBase.findContentEntry(model, item) else null
                if (entry == null) {
                    modifiableModelsProvider.disposeModuleModifiableModel(model)
                    continue
                }
                entry.addDefaultNestExcludes(item!!.parent)
                modifiableModelsProvider.commitModuleModifiableModel(model)
                for (vf in files) {
                    createNestRunConfigurations(module.project, vf.parent)
                }
            }
        }

        override fun equals(other: Any?): Boolean {
            return other is NestJSCLIFrameworkDescription && files == other.files
        }

        override fun hashCode(): Int = files.hashCode()
    }
}