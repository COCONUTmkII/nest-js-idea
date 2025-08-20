package com.coconutmkii.nestjsidea.framework.file

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.util.IconLoader
import com.intellij.ide.FileIconProvider
import javax.swing.Icon

class NestJsFileIconProvider : FileIconProvider {

    private val controllerIcon: Icon = IconLoader.getIcon("/icons/controllerIcon.svg", javaClass)
    private val serviceIcon: Icon = IconLoader.getIcon("/icons/serviceIcon.svg", javaClass)
    private val moduleIcon: Icon = IconLoader.getIcon("/icons/moduleIcon.svg", javaClass)
    private val pipeIcon: Icon = IconLoader.getIcon("/icons/pipeIcon.svg", javaClass)
    private val guardIcon: Icon = IconLoader.getIcon("/icons/guardIcon.svg", javaClass)

    override fun getIcon(file: VirtualFile, flags: Int, project: Project?): Icon? {
        if (file.isDirectory) return null

        // only care about TypeScript files
        val tsFileType = FileTypeManager.getInstance().getFileTypeByExtension("ts")
        if (file.fileType != tsFileType) return null

        val name = file.name.lowercase()

        return when {
            name.endsWith(".controller.ts") -> controllerIcon
            name.endsWith(".service.ts") -> serviceIcon
            name.endsWith(".module.ts") -> moduleIcon
            name.endsWith(".pipe.ts") -> pipeIcon
            name.endsWith(".guard.ts") -> guardIcon
            else -> null
        }
    }
}