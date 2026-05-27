package com.coconutmkii.nestjsidea.services

import com.coconutmkii.nestjsidea.framework.model.NestJSBeanType
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression
import com.intellij.lang.javascript.psi.JSExpression
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.lang.javascript.psi.JSSpreadExpression
import com.intellij.lang.javascript.psi.JSVariable
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.util.JSUtils
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class NestJSBeanService {

    fun isNestJsBeanReferenced(
        clazz: TypeScriptClass,
        project: Project,
        beanType: NestJSBeanType
    ): Boolean {
        val targetClassName = clazz.name ?: return false
        val moduleService = project.service<NestJSModuleService>()
        val allModules = moduleService.findAllNestModules(project)

        return when (beanType) {
            NestJSBeanType.MODULE -> {
                moduleService.isModuleUsedAnywhere(clazz, allModules)
            }
            NestJSBeanType.CONTROLLER -> {
                val controllerService = project.service<NestJSControllerService>()
                allModules.any { module ->
                    controllerService.isControllerDeclared(module, targetClassName)
                }
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

    fun resolveArrayElements(
        expression: JSExpression
    ): Set<String> {
        val result = mutableSetOf<String>()
        when (val unwrapped = JSUtils.unparenthesize(expression)) {
            is JSArrayLiteralExpression -> {
                unwrapped.expressions.forEach { element ->
                    when (element) {
                        is JSReferenceExpression -> {
                            // [A]
                            element.referenceName?.let {
                                result.add(it)
                            }

                            // [...BASE]
                            val resolved = element.resolve()
                            val variable = resolved as? JSVariable
                            val initializer = variable?.initializer

                            if (initializer != null) {
                                result.addAll(resolveArrayElements(initializer))
                            }
                        }
                        is JSSpreadExpression -> {
                            val inner = element.expression ?: return@forEach
                            result.addAll(resolveArrayElements(inner))
                        }
                    }
                }
            }

            is JSReferenceExpression -> {
                // controllers
                val resolved = unwrapped.resolve()
                val variable = resolved as? JSVariable ?: return emptySet()
                val initializer = variable.initializer ?: return emptySet()
                result.addAll(resolveArrayElements(initializer))
            }
        }

        return result
    }
}