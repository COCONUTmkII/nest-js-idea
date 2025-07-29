package com.github.coconutmkii.nestjsidea.window

import com.github.coconutmkii.nestjsidea.NestJSPluginBundle
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.ide.wizard.GeneratorNewProjectWizard
import com.intellij.ide.wizard.GeneratorNewProjectWizardBuilderAdapter
import com.intellij.ide.wizard.GitNewProjectWizardStep
import com.intellij.ide.wizard.NewProjectWizardChainStep.Companion.nextStep
import com.intellij.ide.wizard.NewProjectWizardStep
import com.intellij.ide.wizard.RootNewProjectWizardStep
import com.intellij.ide.wizard.newProjectWizardBaseStepWithoutGap
import javax.swing.Icon

class NestJSCLIProjectToolWindow : GeneratorNewProjectWizard {
    override val id: String = "cli"
    override val icon: Icon = icons.CollaborationToolsIcons.Send
    override val name: String = NestJSPluginBundle.message("nestjs.cli")

    override fun createStep(context: WizardContext): NewProjectWizardStep =
        RootNewProjectWizardStep(context)
            .nextStep(::newProjectWizardBaseStepWithoutGap)
            .nextStep(::GitNewProjectWizardStep)

    class Builder : GeneratorNewProjectWizardBuilderAdapter(NestJSCLIProjectToolWindow())
}