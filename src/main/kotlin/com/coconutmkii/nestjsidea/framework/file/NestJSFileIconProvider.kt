package com.coconutmkii.nestjsidea.framework.file

import com.coconutmkii.nestjsidea.NestJSIcons.controllerIcon
import com.coconutmkii.nestjsidea.NestJSIcons.guardIcon
import com.coconutmkii.nestjsidea.NestJSIcons.moduleIcon
import com.coconutmkii.nestjsidea.NestJSIcons.pipeIcon
import com.coconutmkii.nestjsidea.NestJSIcons.serviceIcon
import com.coconutmkii.nestjsidea.util.isNestProject
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.ide.FileIconProvider
import javax.swing.Icon

class NestJSFileIconProvider : FileIconProvider {

    override fun getIcon(file: VirtualFile, flags: Int, project: Project?): Icon? {
        if (file.isDirectory) return null

        if (project == null || !isNestProject(project)) return null
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
