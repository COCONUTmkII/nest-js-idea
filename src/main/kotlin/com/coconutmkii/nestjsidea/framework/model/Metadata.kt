package com.coconutmkii.nestjsidea.framework.model

import com.intellij.lang.javascript.psi.ecma6.TypeScriptClass

data class NestJsModuleMetadata(
    val controllers: Set<String>,
    val providers: Set<String>,
    val exports: Set<String>,
    val imports: Set<String>,
) {
    fun merge(
        other: NestJsModuleMetadata
    ): NestJsModuleMetadata {

        return NestJsModuleMetadata(
            controllers = controllers + other.controllers,
            providers = providers + other.providers,
            imports = imports + other.imports,
            exports = exports + other.exports
        )
    }

    companion object {
        val EMPTY = NestJsModuleMetadata(
            controllers = emptySet(),
            providers = emptySet(),
            exports = emptySet(),
            imports = emptySet(),
        )
    }
}

data class NestJsModuleIndex(
    val modules: List<TypeScriptClass>,
    val moduleToImports: Map<TypeScriptClass, Set<String>>,
    val moduleToControllers: Map<TypeScriptClass, Set<String>>,
    val moduleToProviders: Map<TypeScriptClass, Set<String>>,
    val moduleToExports: Map<TypeScriptClass, Set<String>>,
)