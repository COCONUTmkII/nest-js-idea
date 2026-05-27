package com.coconutmkii.nestjsidea.services

import com.coconutmkii.nestjsidea.framework.model.NestJSBeanType
import com.coconutmkii.nestjsidea.framework.model.NestJsModuleMetadata
import com.intellij.lang.javascript.TypeScriptFileType
import com.intellij.lang.javascript.psi.JSCallExpression
import com.intellij.lang.javascript.psi.JSObjectLiteralExpression
import com.intellij.lang.javascript.psi.JSReferenceExpression
import com.intellij.lang.javascript.psi.JSReturnStatement
import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass
import com.intellij.lang.javascript.psi.ecma6.TypeScriptFunction
import com.intellij.lang.javascript.psi.stubs.JSClassIndex
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.stubs.StubIndex
import com.intellij.psi.util.CachedValue
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.PsiTreeUtil

@Service(Service.Level.PROJECT)
class NestJSModuleService {

    private val modulesCacheKey =
        Key.create<CachedValue<List<TypeScriptClass>>>(
            "nestjs.modules.cache"
        )

    private val rootModulesCacheKey =
        Key.create<CachedValue<Set<String>>>(
            "nestjs.root.modules.cache"
        )

    fun isRootModule(
        module: TypeScriptClass
    ): Boolean {
        val name = module.name ?: return false
        return findRootModules(module.project)
            .contains(name)
    }

    fun findRootModules(
        project: Project
    ): Set<String> = CachedValuesManager
        .getManager(project)
        .getCachedValue(
            project,
            rootModulesCacheKey,
            {
                CachedValueProvider.Result.create(
                    findRootModulesInternal(project),
                    PsiModificationTracker.MODIFICATION_COUNT
                )
            },
            false
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

    fun isModuleImported(
        module: TypeScriptClass,
        targetModuleName: String
    ): Boolean = buildModuleMetadata(module).imports.contains(targetModuleName)

    fun buildModuleMetadata(module: TypeScriptClass): NestJsModuleMetadata {
        val staticMetadata = extractModuleMetadata(module)
        val dynamicMetadata = extractDynamicModuleMetadata(module)
        return staticMetadata.merge(dynamicMetadata)
    }

    fun isModuleUsedAnywhere(
        targetModule: TypeScriptClass,
        allModules: List<TypeScriptClass>
    ): Boolean {
        val targetName = targetModule.name ?: return false

        if (isRootModule(targetModule)) {
            return true
        }

        return allModules.any { module ->

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
                if (obj.findProperty("module") == null) {
                    continue
                }

                controllers += resolve(obj, "controllers")
                providers += resolve(obj, "providers")
                imports += resolve(obj, "imports")
                exports += resolve(obj, "exports")
            }
        }

        return NestJsModuleMetadata(
            controllers,
            providers,
            imports,
            exports
        )
    }

    fun extractModuleMetadata(module: TypeScriptClass): NestJsModuleMetadata {
        val decoratorService = module.project.service<NestJSDecoratorService>()
        val decorator = decoratorService.findNestDecorator(module, NestJSBeanType.MODULE.normilizedName)
            ?: return NestJsModuleMetadata.EMPTY

        val obj = decoratorService.getObjectLiteralInitializer(decorator)
            ?: return NestJsModuleMetadata.EMPTY

        return NestJsModuleMetadata(
            controllers = resolve(obj, "controllers"),
            providers = resolve(obj, "providers"),
            imports = resolve(obj, "imports"),
            exports = resolve(obj, "exports")
        )
    }

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

    private fun findRootModulesInternal(
        project: Project
    ): Set<String> {

        val result = mutableSetOf<String>()

        val scope =
            GlobalSearchScope.projectScope(project)

        val psiManager =
            PsiManager.getInstance(project)

        FileTypeIndex.processFiles(
            TypeScriptFileType.INSTANCE,
            { virtualFile ->

                val file =
                    psiManager.findFile(virtualFile)
                        ?: return@processFiles true

                val calls =
                    PsiTreeUtil.findChildrenOfType(
                        file,
                        JSCallExpression::class.java
                    )

                for (call in calls) {
                    val method = call.methodExpression as? JSReferenceExpression
                        ?: continue

                    if (method.referenceName != "create") {
                        continue
                    }

                    val qualifier = method.qualifier
                        ?.text
                        ?: continue

                    if (qualifier != "NestFactory") {
                        continue
                    }

                    val firstArg = call.arguments.firstOrNull() as? JSReferenceExpression
                        ?: continue

                    firstArg.referenceName?.let {
                        result.add(it)
                    }
                }
                true
            },
            scope
        )
        return result
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