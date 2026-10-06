package com.coconutmkii.nestjsidea.inspections

import com.coconutmkii.nestjsidea.NestJSBundle
import com.coconutmkii.nestjsidea.framework.model.NestJSBeanType
import com.coconutmkii.nestjsidea.services.NestJSBeanService
import com.coconutmkii.nestjsidea.services.NestJSDecoratorService
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.lang.javascript.psi.JSElementVisitor
import com.intellij.lang.javascript.psi.ecma6.ES6Decorator
import com.intellij.openapi.components.service
import com.intellij.psi.PsiElementVisitor
import org.jetbrains.annotations.PropertyKey

abstract class NestJSBeanNotReferencedInspection(
    private val beanType: NestJSBeanType,
    @param:PropertyKey(resourceBundle = "messages.NestJSBundle")
    private val messageKey: String,
) : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        val project = holder.project
        val beanService = project.service<NestJSBeanService>()
        val decoratorService = project.service<NestJSDecoratorService>()

        return object : JSElementVisitor() {
            override fun visitES6Decorator(decorator: ES6Decorator) {
                if (!decoratorService.isNestSupportedDecorator(decorator, beanType.normalizedName)) return

                val owner = decoratorService.getClassForDecoratorElement(decorator) ?: return
                val name = owner.nameIdentifier ?: return
                if (beanService.isNestJsBeanReferenced(owner, project, beanType)) return

                holder.registerProblem(name, NestJSBundle.message(messageKey))
            }
        }
    }
}
