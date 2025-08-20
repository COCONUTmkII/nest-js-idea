package com.coconutmkii.nestjsidea.window

import com.coconutmkii.nestjsidea.NestJSPluginBundle
import com.coconutmkii.nestjsidea.framework.manager.PackageManager
import com.coconutmkii.nestjsidea.window.step.NestJSStepSetting
import com.intellij.execution.filters.Filter
import com.intellij.ide.util.projectWizard.SettingsStep
import com.intellij.lang.javascript.boilerplate.NpmPackageProjectGenerator
import com.intellij.lang.javascript.boilerplate.NpxPackageDescriptor
import com.intellij.openapi.observable.properties.PropertyGraph
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.ProjectGeneratorPeer
import com.intellij.ui.dsl.builder.panel
import javax.swing.Icon
import javax.swing.JComponent

class NestJSProjectGenerator : NpmPackageProjectGenerator() {
    private val packageName = "@nestjs/cli"
    private val nestCLICommand = "nest"
    override fun getIcon(): Icon? = IconLoader.getIcon("/icons/nestjsIcon.png", javaClass)

    override fun filters(project: Project, baseDir: VirtualFile): Array<out Filter?> = emptyArray()

    override fun customizeModule(baseDir: VirtualFile, entry: ContentEntry?) {
        entry?.addDefaultNestJSExcludes(baseDir)
    }

    override fun createPeer(): ProjectGeneratorPeer<Settings?> {
        return NestJSProjectGeneratorPeer()
    }

    override fun getNpxCommands(): List<NpxPackageDescriptor.NpxCommand?> {
        return listOf(NpxPackageDescriptor.NpxCommand(packageName, nestCLICommand))
    }

    override fun generatorArgs(
        project: Project,
        baseDir: VirtualFile,
        settings: Settings?,
    ): Array<out String?> {
        val selectedManager = if (settings is NestJSStepSetting) {
            settings.packageManager
        } else {
            PackageManager.NPM
        }
        val projectName = project.name
        return arrayOf("new", "--directory", ".", "--package-manager", selectedManager.label, projectName)
    }

    override fun packageName(): String = packageName

    override fun presentablePackageName(): String = NestJSPluginBundle.message("nestjs.presentable.package.name")

    override fun getDescription(): @NlsContexts.DetailedDescription String? = NestJSPluginBundle.message("nestjs.cli.description")

    override fun getName(): @NlsContexts.Label String = NestJSPluginBundle.message("nestjs.cli")

    override fun getId(): String? = NestJSPluginBundle.message("nestjs.cli")

    private fun ContentEntry.addDefaultNestJSExcludes(baseDir: VirtualFile) {
        addExcludeFolder("${baseDir.url}/dist")
    }

    private inner class NestJSProjectGeneratorPeer : NpmPackageGeneratorPeer() {
        var selectedPackageManager: PackageManager = PackageManager.NPM
        val propertyGraph = PropertyGraph()
        val property = propertyGraph.property(selectedPackageManager)
        override fun getComponent(): JComponent {
            return panel {
                row("Package manager") {
                    segmentedButton(
                        items = listOf(PackageManager.NPM, PackageManager.YARN, PackageManager.PNPM),
                        renderer = {
                            text = it.label
                        }
                    )
                        .bind(property)
                        .whenItemSelected {
                            selectedPackageManager = it
                        }
                }
            }
        }

        override fun buildUI(settingStep: SettingsStep) {
            super.buildUI(settingStep)
            settingStep.addSettingsComponent(component)
        }

        override fun getSettings(): Settings {
            return NestJSStepSetting(super.getSettings(), selectedPackageManager)
        }

        override fun isBackgroundJobRunning(): Boolean = false
    }
}
