package com.github.coconutmkii.nestjsidea.framework

import com.github.coconutmkii.nestjsidea.NestJSPluginBundle
import com.github.coconutmkii.nestjsidea.util.isNestJsonFile
import com.github.coconutmkii.nestjsidea.util.isProbableLibraryFile
import com.intellij.framework.FrameworkType
import com.intellij.framework.detection.DetectedFrameworkDescription
import com.intellij.framework.detection.FileContentPattern
import com.intellij.framework.detection.FrameworkDetectionContext
import com.intellij.framework.detection.FrameworkDetector
import com.intellij.json.JsonFileType
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
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
import com.intellij.openapi.vfs.VfsUtilCore

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
                return !isProbableLibraryFile(content.file)
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
                val tmpDir = file.parent.findChild("tmp")
                if (tmpDir != null && ModuleRootManager.getInstance(module).excludeRoots.contains(tmpDir)) {
                    return true
                }
            }
        }
        return false
    }

    override fun getFrameworkType(): FrameworkType = NestJSFramework.INSTANCE

    private inner class NestJSCLIFrameworkDescription(private val files: Collection<VirtualFile>) : DetectedFrameworkDescription() {
        override fun getRelatedFiles(): Collection<VirtualFile?> = files

        override fun getSetupText(): @NlsContexts.Label String = NestJSPluginBundle.message("nestjs.cli")

        override fun getDetector(): FrameworkDetector = this@NestJSFrameworkDetector

        override fun setupFramework(
            modifiableModelsProvider: ModifiableModelsProvider,
            modulesProvider: ModulesProvider
        ) {
            for (module in modulesProvider.modules) {
                val model = modifiableModelsProvider.getModuleModifiableModel(module)
                val item = files.firstOrNull()
                val entry = model.contentEntries.find {
                    it.file != null && VfsUtilCore.isAncestor(it.file!!, item!!, false)
                } ?: continue
                item!!.parent.findChild("tmp")?.let {
                    contentEntry -> entry.addExcludeFolder(contentEntry)
                }
                createNestRunConfigurations(module.project)
            }
        }

        override fun equals(other: Any?): Boolean {
            return other is NestJSCLIFrameworkDescription && files == other.files
        }

        override fun hashCode(): Int = files.hashCode()

        private fun createNestRunConfigurations(project: Project) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("NestJS Detected")
                .createNotification("NestJS project detected",
                    "To run your app, add an External Tool: npm run start",
                    NotificationType.INFORMATION)
                .notify(project)
        }
    }
}