package com.coconutmkii.nestjsidea.services

import com.coconutmkii.nestjsidea.framework.model.DYNAMIC_MODULE_DISCRIMINATOR
import com.coconutmkii.nestjsidea.framework.model.NestJSBeanType
import com.coconutmkii.nestjsidea.framework.model.NestJSModuleProperty
import com.coconutmkii.nestjsidea.framework.model.NestJsModuleMetadata
import com.coconutmkii.nestjsidea.index.NestJSDecoratorIndex
import com.coconutmkii.nestjsidea.index.NestJSRootModuleIndex
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.JSReturnStatement
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecma6.TypeScriptFunction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.indexing.FileBasedIndex

@Service(Service.Level.PROJECT)
class NestJSModuleService {

    private val modulesCacheKey =
        Key.create<CachedValue<List<TypeScriptClass>>>(
            "nestjs.modules.cache"
        )

    fun isRootModule(
        module: TypeScriptClass
    ): Boolean {
        val name = module.name ?: return false

        return FileBasedIndex.getInstance()
            .getContainingFiles(
                NestJSRootModuleIndex.KEY,
                name,
                GlobalSearchScope.projectScope(module.project)
            )
            .isNotEmpty()
    }

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

    fun isModuleImported(
        module: TypeScriptClass,
        targetModuleName: String
    ): Boolean = buildModuleMetadata(module).imports.contains(targetModuleName)

    fun buildModuleMetadata(module: TypeScriptClass): NestJsModuleMetadata =
        CachedValuesManager.getCachedValue(module) {
            CachedValueProvider.Result.create(
                extractModuleMetadata(module).merge(extractDynamicModuleMetadata(module)),
                module.containingFile
            )
        }

    fun isModuleUsedAnywhere(
        targetModule: TypeScriptClass,
        allModules: List<TypeScriptClass>
    ): Boolean {
        val targetName = targetModule.name ?: return false

        return isRootModule(targetModule) || allModules.any { module ->

            // don't self-check
            if (module == targetModule) {
                return@any false
            }

            isModuleImported(
                module,
                targetName
            )
        }
    }


    fun extractDynamicModuleMetadata(
        module: TypeScriptClass
    ): NestJsModuleMetadata {
        val controllers = mutableSetOf<String>()
        val providers = mutableSetOf<String>()
        val imports = mutableSetOf<String>()
        val exports = mutableSetOf<String>()

        val functions =
            PsiTreeUtil.findChildrenOfType(
                module,
                TypeScriptFunction::class.java
            )

        for (function in functions) {

            val returnStatements =
                PsiTreeUtil.findChildrenOfType(
                    function,
                    JSReturnStatement::class.java
                )

            for (statement in returnStatements) {
                val obj = statement.expression as? JSObjectLiteralExpression
                        ?: continue

                // DynamicModule objects always contain "module"
                if (obj.findProperty(DYNAMIC_MODULE_DISCRIMINATOR) == null) {
                    continue
                }

                controllers += resolve(obj, NestJSModuleProperty.CONTROLLERS.key)
                providers += resolve(obj, NestJSModuleProperty.PROVIDERS.key)
                imports += resolve(obj, NestJSModuleProperty.IMPORTS.key)
                exports += resolve(obj, NestJSModuleProperty.EXPORTS.key)
            }
        }

        return NestJsModuleMetadata(
            controllers = controllers,
            providers = providers,
            exports = exports,
            imports = imports
        )
    }

    fun extractModuleMetadata(module: TypeScriptClass): NestJsModuleMetadata {
        val decoratorService = module.project.service<NestJSDecoratorService>()
        val decorator = decoratorService.findNestDecorator(module, NestJSBeanType.MODULE.normalizedName)
            ?: return NestJsModuleMetadata.EMPTY

        val obj = decoratorService.getObjectLiteralInitializer(decorator)
            ?: return NestJsModuleMetadata.EMPTY

        return NestJsModuleMetadata(
            controllers = resolve(obj, NestJSModuleProperty.CONTROLLERS.key),
            providers = resolve(obj, NestJSModuleProperty.PROVIDERS.key),
            imports = resolve(obj, NestJSModuleProperty.IMPORTS.key),
            exports = resolve(obj, NestJSModuleProperty.EXPORTS.key)
        )
    }

    private fun findAllNestModulesInternal(
        project: Project
    ): List<TypeScriptClass> {
        val scope = GlobalSearchScope.projectScope(project)
        val decoratorService = project.service<NestJSDecoratorService>()
        val psiManager = PsiManager.getInstance(project)

        val files = FileBasedIndex.getInstance()
            .getContainingFiles(NestJSDecoratorIndex.KEY, NestJSBeanType.MODULE.normalizedName, scope)

        return files.flatMap { vf ->
            val psiFile = psiManager.findFile(vf) ?: return@flatMap emptyList()
            PsiTreeUtil.findChildrenOfType(psiFile, TypeScriptClass::class.java)
                .filter { decoratorService.findNestDecorator(it, NestJSBeanType.MODULE.normalizedName) != null }
        }
    }

    private fun resolve(
        obj: JSObjectLiteralExpression,
        key: String
    ): Set<String> {

        val expr = obj.findProperty(key)
            ?.initializer
            ?: return emptySet()

        return obj.project.service<NestJSBeanService>().resolveArrayElements(expr)
    }


}
