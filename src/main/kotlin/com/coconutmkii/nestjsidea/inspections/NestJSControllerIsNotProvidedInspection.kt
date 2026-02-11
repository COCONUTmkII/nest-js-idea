package com.coconutmkii.nestjsidea.inspections

import com.coconutmkii.nestjsidea.NestJSBundle
import com.coconutmkii.nestjsidea.services.NestJSDecoratorService
import com.coconutmkii.nestjsidea.services.NestJSDecoratorService.CONTROLLER_DECORATOR
import com.coconutmkii.nestjsidea.services.NestJSDecoratorService.isNestSupportedDecorator
import com.coconutmkii.nestjsidea.services.NestJSModuleService.isClassImportedInAnyModule
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.javascript.psi.JSElementVisitor
import com.intellij.lang.javascript.psi.ecma6.ES6Decorator
import com.intellij.psi.PsiElementVisitor

class NestJSControllerIsNotProvidedInspection : LocalInspectionTool() {
    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : JSElementVisitor() {
            override fun visitES6Decorator(decorator: ES6Decorator) {
                if (isNestSupportedDecorator(decorator, CONTROLLER_DECORATOR)) {
                    val classOfDecorator = NestJSDecoratorService.getClassForDecoratorElement(decorator) ?: return
                    val nameOfProvidedClassWithDecorator = classOfDecorator.nameIdentifier ?: return
                    if (!isClassImportedInAnyModule(classOfDecorator, holder.project)) {
                        holder.registerProblem(nameOfProvidedClassWithDecorator, NestJSBundle.message("nestjs.inspection.controller.is.not.provided"))
                    }
                }
            }
        }
    }
}
