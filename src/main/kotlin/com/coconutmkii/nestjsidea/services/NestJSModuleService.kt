package com.coconutmkii.nestjsidea.services

import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.stubs.JSClassIndex
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker

@Service(Service.Level.PROJECT)
class NestJSModuleService {

    private val modulesCacheKey =
        Key.create<CachedValue<List<TypeScriptClass>>>(
            "nestjs.modules.cache"
        )

    fun findAllNestModules(
        project: Project
    ): List<TypeScriptClass> = CachedValuesManager
        .getManager(project)
        .getCachedValue(
            project,
            modulesCacheKey,
            {
                CachedValueProvider.Result.create(
                    findAllNestModulesInternal(project),
                    PsiModificationTracker.MODIFICATION_COUNT
                )
            },
            false
        )

    /**
    * IMPORTANT:
    * We must NOT call JSClassIndex.getElements()
    * inside processAllKeys().
    *
    * Otherwise, IntelliJ throws:
    *
    * IllegalStateException:
    * Nesting processElements call under other
    * stub index operation can lead to a deadlock.
    */
    private fun findAllNestModulesInternal(
        project: Project
    ): List<TypeScriptClass> {

        val scope = GlobalSearchScope.projectScope(project)
        val decoratorService = project.service<NestJSDecoratorService>()

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

                if (decoratorService.findNestDecorator(clazz, NestJSBeanType.MODULE.normilizedName) != null) {
                    result += clazz
                }
            }
        }

        return result
    }
}