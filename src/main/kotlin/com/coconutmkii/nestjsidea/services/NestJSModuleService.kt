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
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.PsiTreeUtil.getStubChildrenOfTypeAsList

@Service
object NestJSModuleService {

    fun findAllNestModules(project: Project): List<TypeScriptClass> {
        val scope = GlobalSearchScope.projectScope(project)
        val result = mutableListOf<TypeScriptClass>()
        StubIndex.getInstance().processAllKeys(JSClassIndex.KEY, project) { key ->
            val elements = JSClassIndex.getElements(key, project, scope)
            for (el in elements) {
                val clazz = el as? TypeScriptClass ?: continue
                if (findNestDecorator(clazz, "Module") != null) {
                    result += clazz
                }
            }
            true
        }
        return result
    }

    @JvmStatic
    @StubSafe
    fun isClassImportedInAnyModule(clazz: TypeScriptClass, project: Project): Boolean {
        val allModules = findAllNestModules(project)

        for (module in allModules) {
            val decorator = module.attributeList
                ?.let { getStubChildrenOfTypeAsList(it, ES6Decorator::class.java) }
                ?.firstOrNull { isNestSupportedDecorator(it, MODULE_DECORATOR) }
                ?: continue

            val initializer = getObjectLiteralInitializer(decorator) ?: continue
            val importsArray = initializer.findProperty(CONTROLLERS_PROVIDER)?.initializer
            if (importsArray != null && arrayContainsClass(importsArray, clazz)) {
                return true
            }
        }
        return false
    }

    fun arrayContainsClass(expr: JSExpression, clazz: TypeScriptClass): Boolean {
        val arrayExpr = JSUtils.unparenthesize(expr) as? JSArrayLiteralExpression ?: return false
        return arrayExpr.expressions.any {
            (it as? JSReferenceExpression)?.resolve() == clazz
        }
    }
}
