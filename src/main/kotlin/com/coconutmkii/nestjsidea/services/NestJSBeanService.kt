package com.coconutmkii.nestjsidea.services

import com.coconutmkii.nestjsidea.framework.model.NestJSBeanType
import com.coconutmkii.nestjsidea.framework.model.NestJSModuleProperty
import com.coconutmkii.nestjsidea.framework.model.NestJSProviderProperty
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSConditionalExpression
import com.intellij.lang.javascript.psi.JSExpression
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.lang.javascript.psi.JSSpreadExpression
import com.intellij.lang.javascript.psi.JSVariable
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.util.JSUtils
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil

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

    fun resolveArrayElements(expression: JSExpression): Set<String> =
        resolveArrayElements(expression, mutableSetOf(), mutableSetOf())

    private fun resolveArrayElements(
        expression: JSExpression,
        visited: MutableSet<PsiElement>,
        result: MutableSet<String>,
    ): Set<String> {
        if (!visited.add(expression)) return result

        when (val unwrapped = JSUtils.unparenthesize(expression)) {
            is JSArrayLiteralExpression ->
                unwrapped.expressions.forEach { collectElement(it, visited, result) }

            is JSReferenceExpression ->
                (unwrapped.resolve() as? JSVariable)?.initializer
                    ?.let { resolveArrayElements(it, visited, result) }

            // [...(isProd ? [ProdModule] : [])]
            is JSConditionalExpression -> {
                unwrapped.thenBranch?.let { resolveArrayElements(it, visited, result) }
                unwrapped.elseBranch?.let { resolveArrayElements(it, visited, result) }
            }
        }
        return result
    }

    private fun collectElement(
        element: JSExpression?,
        visited: MutableSet<PsiElement>,
        result: MutableSet<String>,
    ) {
        when (val e = JSUtils.unparenthesize(element ?: return)) {
            is JSReferenceExpression -> {
                e.referenceName?.let(result::add)
                (e.resolve() as? JSVariable)?.initializer
                    ?.let { resolveArrayElements(it, visited, result) }
            }
            is JSSpreadExpression ->
                resolveArrayElements(e.expression ?: return, visited, result)
            is JSCallExpression -> {
                val method = e.methodExpression as? JSReferenceExpression
                when {
                    // [ConfigModule.forRoot()], [TypeOrmModule.forFeature([...])]
                    method?.qualifier != null ->
                        (method.qualifier as? JSReferenceExpression)?.referenceName?.let(result::add)

                    // [forwardRef(() => UserModule)]
                    method?.referenceName == NestJSModuleProperty.FORWARD_REF.providerKey ->
                        e.arguments.firstOrNull()
                            ?.let { PsiTreeUtil.findChildrenOfType(it, JSReferenceExpression::class.java) }
                            ?.forEach { ref -> ref.referenceName?.let(result::add) }
                }
            }

            // [{ provide: TOKEN, useClass: MyService }]
            is JSObjectLiteralExpression -> {
                NestJSProviderProperty.entries.forEach { provider ->
                    (e.findProperty(provider.key)?.initializer as? JSReferenceExpression)
                        ?.referenceName?.let(result::add)
                }
            }

            // [isProd ? ProdModule : DevModule]
            is JSConditionalExpression -> {
                collectElement(e.thenBranch, visited, result)
                collectElement(e.elseBranch, visited, result)
            }
        }
    }
}