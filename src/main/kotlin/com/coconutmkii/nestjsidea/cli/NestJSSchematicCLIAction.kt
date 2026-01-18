package com.coconutmkii.nestjsidea.cli

import com.coconutmkii.nestjsidea.NestJSBundle
import com.coconutmkii.nestjsidea.NestJSIcons.nestIcon
import com.coconutmkii.nestjsidea.cli.NestJSSupportedSchematics.supportedSchematics
import com.coconutmkii.nestjsidea.services.NestJSNotificationService.notifyNestCLIWasNotFound
import com.coconutmkii.nestjsidea.util.NESTJS_CLI_PACKAGE
import com.coconutmkii.nestjsidea.util.findNestJSCliFolder
import com.coconutmkii.nestjsidea.util.getNestCliPackageVersion
import com.intellij.CommonBundle
import com.intellij.icons.AllIcons
import com.intellij.javascript.nodejs.CompletionModuleInfo
import com.intellij.javascript.nodejs.NodeModuleSearchUtil
import com.intellij.javascript.nodejs.interpreter.NodeJsInterpreterManager
import com.intellij.javascript.nodejs.util.NodePackage
import com.intellij.lang.javascript.JavaScriptBundle
import com.intellij.lang.javascript.boilerplate.NpmPackageProjectGenerator
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.PlatformCoreDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.IconButton
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.CollectionListModel
import com.intellij.ui.ColoredListCellRenderer
import com.intellij.ui.DoubleClickListener
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.components.JBList
import com.intellij.ui.scale.JBUIScale
import com.intellij.ui.speedSearch.ListWithFilter
import com.intellij.util.text.SemVer
import com.intellij.util.ui.EmptyIcon
import com.intellij.util.ui.JBScalableIcon
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.UIUtil
import java.awt.Component
import java.awt.Dimension
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import java.awt.event.MouseEvent
import javax.swing.JList

class NestJSSchematicCLIAction() : DumbAwareAction(nestIcon) {
    override fun update(e: AnActionEvent) {
        val project = e.project ?: return
        val file = e.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return
        e.presentation.isEnabledAndVisible = findNestJSCliFolder(project, file) != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val file = e.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return
        val editor = e.getData(PlatformCoreDataKeys.FILE_EDITOR)
        val cli = findNestJSCliFolder(project, file) ?: return
        val cliVersion = getNestCliPackageVersion(cli)

        if (cliVersion == null) {
            notifyNestCLIWasNotFound(project)
            return
        }

        val schemaModel = CollectionListModel(supportedSchematics)
        val list = JBList(schemaModel)
        list.cellRenderer = object : ColoredListCellRenderer<NestJSSchematic>() {
            override fun customizeCellRenderer(
                list: JList<out NestJSSchematic>,
                value: NestJSSchematic,
                index: Int,
                selected: Boolean,
                hasFocus: Boolean
            ) {
                if (!selected && index % 2 == 0) {
                    background = UIUtil.getDecoratedRowColor()
                }
                icon = JBUIScale.scaleIcon(EmptyIcon.create(5) as JBScalableIcon)
                append(value.name, SimpleTextAttributes.REGULAR_ATTRIBUTES, true)
                append(" - " + value.description, SimpleTextAttributes.GRAY_ATTRIBUTES, false)
            }
        }
        val actionGroup = DefaultActionGroup()
        val actionToolbar =
            ActionManager.getInstance().createActionToolbar("NestCliGenerate", actionGroup, true).apply {
                isReservePlaceAutoPopupIcon = false
                minimumButtonSize = Dimension(22, 22)
            }
        val toolbarComponent = actionToolbar.component.apply { isOpaque = false }

        val scroll = ScrollPaneFactory.createScrollPane(list)
        scroll.border = JBUI.Borders.empty()
        val pane = ListWithFilter.wrap(list, scroll) { obj: Any -> obj.toString() }

        val builder = JBPopupFactory
            .getInstance()
            .createComponentPopupBuilder(pane, list)
            .setMayBeParent(true)
            .setRequestFocus(true)
            .setFocusable(true)
            .setFocusOwners(arrayOf<Component>(list))
            .setLocateWithinScreenBounds(true)
            .setCancelOnOtherWindowOpen(true)
            .setMovable(true)
            .setResizable(true)
            .setTitle(NestJSBundle.message("nestjs.dialog.schematic.title"))
            .setSettingButtons(toolbarComponent)
            .setCancelOnWindowDeactivation(false)
            .setCancelOnClickOutside(true)
            .setDimensionServiceKey(project, "org.angular.cli.generate", true)
            .setMinSize(Dimension(JBUI.scale(350), JBUI.scale(300)))
            .setCancelButton(
                IconButton(
                    CommonBundle.message("action.text.close"),
                    AllIcons.Actions.Close,
                    AllIcons.Actions.CloseHovered
                )
            )
        val popup = builder.createPopup()
        list.addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent?) {
                if (list.selectedValue == null) return
                if (e?.keyCode == KeyEvent.VK_ENTER) {
                    e.consume()
                    askOptions(project, popup, list.selectedValue as NestJSSchematic, cli, workingDir(editor, file), cliVersion)
                }
            }
        })
        object : DoubleClickListener() {
            override fun onDoubleClick(event: MouseEvent): Boolean {
                if (list.selectedValue == null) return true
                askOptions(project, popup, list.selectedValue as NestJSSchematic, cli, workingDir(editor, file), cliVersion)
                return true
            }
        }.installOn(list)
        popup.showCenteredInCurrentWindow(project)
    }

    private fun workingDir(editor: FileEditor?, file: VirtualFile?): VirtualFile? {
        if (editor == null && file != null) {
            return if (file.isDirectory) file else file.parent
        }
        return null
    }

    private fun askOptions(project: Project,
                           popup: JBPopup,
                           schematic: NestJSSchematic,
                           cli: VirtualFile,
                           workingDir: VirtualFile?,
                           cliVersion: SemVer
    ) {
        popup.closeOk(null)

        val dialog = NestJSSchematicOptionsDialog(project, schematic, cliVersion)
        if (dialog.showAndGet()) {
            ApplicationManager.getApplication().executeOnPooledThread {
                runGenerator(project, schematic, dialog.arguments(), cli, workingDir)
            }
        }
    }

    private fun runGenerator(project: Project, schematic: NestJSSchematic, arguments: Array<String>, cli: VirtualFile, workingDir: VirtualFile?) {
        val interpreter = NodeJsInterpreterManager.getInstance(project).interpreter ?: return

        val modules: MutableList<CompletionModuleInfo> = mutableListOf()
        NodeModuleSearchUtil.findModulesWithName(modules, NESTJS_CLI_PACKAGE, cli, null)

        val module = modules.firstOrNull() ?: return

        val filter = NestJSCliFilter(project, cli.path)
        val title = checkNotNull(schematic.name)
        NpmPackageProjectGenerator.generate(
            interpreter,
            NodePackage(module.virtualFile?.path!!),
            { module ->
                module.findBinFile("nest", null)?.path
                    ?: throw IllegalStateException("Nest CLI binary not found")
            },
            cli,
            VfsUtilCore.virtualToIoFile(workingDir ?: cli),
            project,
            null,
            JavaScriptBundle.message("generating.0", cli.name),
            arrayOf(filter),
            "generate",
            title,
            *arguments)
    }
}
