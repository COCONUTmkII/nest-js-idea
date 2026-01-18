package com.coconutmkii.nestjsidea.cli

import com.coconutmkii.nestjsidea.NestJSBundle
import com.intellij.execution.configurations.CommandLineTokenizer
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.EditorTextField
import com.intellij.util.text.SemVer
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class NestJSSchematicOptionsDialog(
    val project: Project,
    private val schematic: NestJSSchematic,
    private val cliVersion: SemVer
) : DialogWrapper(project, true) {
    private lateinit var editor: EditorTextField

    init {
        title = NestJSBundle.message("nestjs.dialog.schematic.options")
        init()
    }

    override fun createCenterPanel(): JComponent? {
        val panel = JPanel(BorderLayout(0, 4)).apply {
            add(JLabel(schematic.description), BorderLayout.NORTH)
        }
        editor = NestJSSchematicOptionsTextField(project, schematic.options, cliVersion)
        panel.add(editor, BorderLayout.CENTER)
        return panel
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return editor
    }

    fun arguments(): Array<String> {
        val tokenizer = CommandLineTokenizer(editor.text)
        val result: MutableList<String> = mutableListOf()
        while (tokenizer.hasMoreTokens()) {
            result.add(tokenizer.nextToken())
        }
        return result.toTypedArray()
    }

}
