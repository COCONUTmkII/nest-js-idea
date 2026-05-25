package com.coconutmkii.nestjsidea.services

import com.intellij.lang.javascript.psi.StubSafe
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

enum class NestJSBeanType(val normilizedName: String) {
    MODULE("module"),
    CONTROLLER("controller"),
    SERVICE("service"),
    GUARD("guard"),
    PIPE("pipe"),
    RESOLVER("resolver"),
}

@Service(Service.Level.PROJECT)
class NestJSBeanService {

    @StubSafe
    fun checkDoesBeanIsUsedInAnyModule(
        clazz: TypeScriptClass,
        project: Project,
        beanType: NestJSBeanType
    ): Boolean {
        val targetClassName = clazz.name ?: return false
        val moduleService = project.service<NestJSModuleService>()
        val allModules = moduleService.findAllNestModules(project)

        return when (beanType) {
            NestJSBeanType.MODULE -> {
                true
            }
            NestJSBeanType.CONTROLLER -> {
                val controllerService = project.service<NestJSControllerService>()
                allModules.forEach { module ->

                    val dynamic =
                        controllerService.isControllerDeclaredInDynamicModule(
                            module,
                            targetClassName
                        )

                    val static =
                        controllerService.isControllerDeclaredInStaticModule(
                            module,
                            targetClassName
                        )

                    val result = dynamic || static

                    if (result) {
                        return true
                    }
                }
                false
            }
            NestJSBeanType.SERVICE -> {
                true
            }
            NestJSBeanType.GUARD -> {
                true
            }
            NestJSBeanType.PIPE -> {
                true
            }
            NestJSBeanType.RESOLVER -> {
                true
            }
        }
    }

}