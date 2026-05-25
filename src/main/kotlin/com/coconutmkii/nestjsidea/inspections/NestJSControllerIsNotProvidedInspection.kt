package com.coconutmkii.nestjsidea.inspections

import com.coconutmkii.nestjsidea.NestJSBundle
import com.coconutmkii.nestjsidea.services.NestJSBeanService
import com.coconutmkii.nestjsidea.services.NestJSBeanType
import com.coconutmkii.nestjsidea.services.NestJSDecoratorService
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.javascript.psi.JSElementVisitor
import com.intellij.lang.javascript.psi.ecma6.ES6Decorator
import com.intellij.openapi.components.service
import com.intellij.psi.PsiElementVisitor

class NestJSControllerIsNotProvidedInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JSElementVisitor() {
            override fun visitES6Decorator(decorator: ES6Decorator) {
                val beanService = holder.project.service<NestJSBeanService>()
                val decoratorService = holder.project.service<NestJSDecoratorService>()
                if (decoratorService.isNestSupportedDecorator(decorator, NestJSBeanType.CONTROLLER.normilizedName)) {
                    val classOfDecorator = decoratorService.getClassForDecoratorElement(decorator) ?: return
                    val nameOfProvidedClassWithDecorator = classOfDecorator.nameIdentifier ?: return

                    if (!beanService.checkDoesBeanIsUsedInAnyModule(classOfDecorator, holder.project, NestJSBeanType.CONTROLLER)) {
                        holder.registerProblem(nameOfProvidedClassWithDecorator, NestJSBundle.message("nestjs.inspection.controller.is.not.provided"))
                    }
                }
            }
        }
    }
}
