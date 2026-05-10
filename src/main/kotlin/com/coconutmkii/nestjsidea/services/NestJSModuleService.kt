package com.coconutmkii.nestjsidea.services

import com.coconutmkii.nestjsidea.services.NestJSDecoratorService.MODULE_DECORATOR
import com.coconutmkii.nestjsidea.services.NestJSDecoratorService.findNestDecorator
import com.coconutmkii.nestjsidea.services.NestJSDecoratorService.getObjectLiteralInitializer
import com.coconutmkii.nestjsidea.services.NestJSDecoratorService.isNestSupportedDecorator
import com.coconutmkii.nestjsidea.util.CONTROLLERS_PROVIDER
import com.intellij.lang.javascript.psi.JSArrayLiteralExpression
import com.intellij.lang.javascript.psi.JSExpression
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.lang.javascript.psi.StubSafe
import com.intellij.lang.javascript.psi.ecma6.ES6Decorator
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.stubs.JSClassIndex
import com.intellij.lang.javascript.psi.util.JSUtils
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.PsiTreeUtil.getStubChildrenOfTypeAsList

@Service
object NestJSModuleService {

    private val MODULES_CACHE_KEY =
        Key.create<CachedValue<List<TypeScriptClass>>>(
            "nestjs.modules.cache"
        )

    fun findAllNestModules(
        project: Project
    ): List<TypeScriptClass> = CachedValuesManager
        .getManager(project)
        .getCachedValue(
            project,
            MODULES_CACHE_KEY,
            {
                CachedValueProvider.Result.create(
                    findAllNestModulesInternal(project),
                    PsiModificationTracker.MODIFICATION_COUNT
                )
            },
            false
        )

    private fun findAllNestModulesInternal(
        project: Project
    ): List<TypeScriptClass> {

        val scope = GlobalSearchScope.projectScope(project)

        /*
         * IMPORTANT:
         * We must NOT call JSClassIndex.getElements()
         * inside processAllKeys().
         *
         * Otherwise IntelliJ throws:
         *
         * IllegalStateException:
         * Nesting processElements call under other
         * stub index operation can lead to a deadlock.
         */
        val keys = mutableListOf<String>()

        StubIndex.getInstance().processAllKeys(
            JSClassIndex.KEY,
            project
        ) { key ->
            keys += key
            true
        }

        val result = mutableListOf<TypeScriptClass>()

        for (key in keys) {
            val elements = JSClassIndex.getElements(
                key,
                project,
                scope
            )

            for (element in elements) {
                val clazz = element as? TypeScriptClass
                    ?: continue

                if (findNestDecorator(clazz, MODULE_DECORATOR) != null) {
                    result += clazz
                }
            }
        }

        return result
    }

    @JvmStatic
    @StubSafe
    fun isClassImportedInAnyModule(
        clazz: TypeScriptClass,
        project: Project
    ): Boolean {

        val targetClassName = clazz.name ?: return false

        val allModules = findAllNestModules(project)

        for (module in allModules) {

            val decorator = module.attributeList
                ?.let {
                    getStubChildrenOfTypeAsList(
                        it,
                        ES6Decorator::class.java
                    )
                }
                ?.firstOrNull {
                    isNestSupportedDecorator(
                        it,
                        MODULE_DECORATOR
                    )
                }
                ?: continue

            val initializer =
                getObjectLiteralInitializer(decorator)
                    ?: continue

            val controllersArray = initializer
                .findProperty(CONTROLLERS_PROVIDER)
                ?.initializer
                ?: continue

            if (arrayContainsClass(controllersArray, targetClassName)) {
                return true
            }
        }

        return false
    }

    private fun arrayContainsClass(
        expression: JSExpression,
        targetClassName: String
    ): Boolean {

        val arrayExpression =
            JSUtils.unparenthesize(expression)
                    as? JSArrayLiteralExpression
                ?: return false

        return arrayExpression.expressions.any { expr ->

            val reference =
                expr as? JSReferenceExpression
                    ?: return@any false

            reference.referenceName == targetClassName
        }
    }
}