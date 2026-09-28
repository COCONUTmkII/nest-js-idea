package com.coconutmkii.nestjsidea.services

import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class NestJSControllerService(private val project: Project) {

    fun isControllerUsedAnywhere(
        controller: TypeScriptClass,
        allModules: List<TypeScriptClass>
    ): Boolean {
        val targetName = controller.name ?: return false
        val moduleService = project.service<NestJSModuleService>()

        return allModules.any { module ->
            moduleService.buildModuleMetadata(module)
                .controllers
                .contains(targetName)
        }
    }
}
