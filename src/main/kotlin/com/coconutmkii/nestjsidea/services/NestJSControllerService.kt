package com.coconutmkii.nestjsidea.services

import com.coconutmkii.nestjsidea.util.CONTROLLERS_PROVIDER
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression
import com.intellij.lang.javascript.psi.JSExpression
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.lang.javascript.psi.JSReturnStatement
import com.intellij.lang.javascript.psi.JSSpreadExpression
import com.intellij.lang.javascript.psi.JSVariable
import com.intellij.lang.javascript.psi.StubSafe
import com.intellij.lang.javascript.psi.ecma6.ES6Decorator
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecma6.TypeScriptFunction
import com.intellij.lang.javascript.psi.util.JSUtils
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.PsiTreeUtil.getStubChildrenOfTypeAsList

@Service(Service.Level.PROJECT)
class NestJSControllerService {

    @StubSafe
    fun isControllerDeclaredInStaticModule(
        module: TypeScriptClass,
        targetClassName: String
    ): Boolean {
        val decoratorService = module.project.service<NestJSDecoratorService>()
        val decorator = module.attributeList
            ?.let {
                getStubChildrenOfTypeAsList(
                    it,
                    ES6Decorator::class.java
                )
            }
            ?.firstOrNull {
                decoratorService.isNestSupportedDecorator(
                    it,
                    NestJSBeanType.MODULE.normilizedName
                )
            }
            ?: return false

        val initializer =
            decoratorService.getObjectLiteralInitializer(decorator)
                ?: return false

        val controllersArray = initializer
            .findProperty(CONTROLLERS_PROVIDER)
            ?.initializer
            ?: return false

        return containsClass(
            controllersArray,
            targetClassName
        )
    }

    @StubSafe
    fun isControllerDeclaredInDynamicModule(
        module: TypeScriptClass,
        targetClassName: String
    ): Boolean {
        val functions = PsiTreeUtil.findChildrenOfType(
            module,
            TypeScriptFunction::class.java
        )

        for (function in functions) {
            val returnStatements = PsiTreeUtil.findChildrenOfType(
                function,
                JSReturnStatement::class.java
            )

            for (returnStatement in returnStatements) {

                val objectLiteral =
                    returnStatement.expression
                            as? JSObjectLiteralExpression
                        ?: continue

                if (!isNestDynamicModuleObject(objectLiteral)) {
                    continue
                }

                val controllersInitializer =
                    objectLiteral
                        .findProperty(CONTROLLERS_PROVIDER)
                        ?.initializer
                        ?: continue

                if (
                    containsClass(
                        controllersInitializer,
                        targetClassName
                    )
                ) {
                    return true
                }
            }
        }

        return false
    }

    private fun isNestDynamicModuleObject(
        objectLiteral: JSObjectLiteralExpression
    ): Boolean = objectLiteral.findProperty(NestJSBeanType.MODULE.normilizedName) != null

    private fun resolveArrayElements(
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
                            val resolved =
                                element.resolve()

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

    private fun containsClass(
        expression: JSExpression,
        targetClassName: String
    ): Boolean = resolveArrayElements(expression).contains(targetClassName)
}