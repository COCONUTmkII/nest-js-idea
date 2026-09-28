package com.coconutmkii.nestjsidea.framework.model

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

const val DYNAMIC_MODULE_DISCRIMINATOR = "module"

const val FORWARD_REF_FUNCTION = "forwardRef"

enum class NestJSModuleProperty(val key: String) {
    IMPORTS("imports"),
    CONTROLLERS("controllers"),
    EXPORTS("exports"),
    PROVIDERS("providers"),
}

enum class NestJSProviderProperty(val key: String) {
    PROVIDE("provide"),
    USE_CLASS("useClass"),
    USE_EXISTING("useExisting"),
}
