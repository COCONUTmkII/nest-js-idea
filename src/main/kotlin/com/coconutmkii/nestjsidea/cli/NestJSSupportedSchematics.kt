package com.coconutmkii.nestjsidea.cli

object NestJSSupportedSchematics {
    val supportedGenerateSchematicOptions = listOf("--dry-run", "--project", "--flat", "--collection", "--spec", "--no-spec")
    val schematicOptionDescriptions = mapOf(
        "--dry-run" to "Reports changes that would be made, but does not change the filesystem.",
        "--project" to "Project that element should be added to.",
        "--flat" to "Do not generate a folder for the element.",
        "--collection" to "Specify schematics collection. Use package name of installed npm package containing schematic.",
        "--spec" to "Enforce spec files generation (default).",
        "--no-spec" to "Disable spec files generation."
    )
    val supportedSchematics = listOf(
        NestJSSchematic(name = "app", description = "Generate a new application within a monorepo (converting to monorepo if it's a standard structure).", options = supportedGenerateSchematicOptions, arguments = listOf()),
        NestJSSchematic(name = "library", description = "Generate a new library within a monorepo (converting to monorepo if it's a standard structure).", options = supportedGenerateSchematicOptions, arguments = listOf()),
        NestJSSchematic(name = "class", description = "Generate a new class.", options = supportedGenerateSchematicOptions, arguments = listOf()),
        NestJSSchematic(name = "controller", description = "Generate a controller declaration.", options = supportedGenerateSchematicOptions, arguments = listOf()),
        NestJSSchematic(name = "decorator", description = "Generate a custom decorator.", options = supportedGenerateSchematicOptions, arguments = listOf()),
        NestJSSchematic(name = "filter", description = "Generate a filter declaration.", options = supportedGenerateSchematicOptions, arguments = listOf()),
        NestJSSchematic(name = "gateway", description = "Generate a gateway declaration.", options = supportedGenerateSchematicOptions, arguments = listOf()),
        NestJSSchematic(name = "guard", description = "Generate a guard declaration.", options = supportedGenerateSchematicOptions, arguments = listOf()),
        NestJSSchematic(name = "interface", description = "Generate an interface.", options = supportedGenerateSchematicOptions, arguments = listOf()),
        NestJSSchematic(name = "interceptor", description = "Generate an interceptor declaration.", options = supportedGenerateSchematicOptions, arguments = listOf()),
        NestJSSchematic(name = "middleware", description = "Generate a middleware declaration.", options = supportedGenerateSchematicOptions, arguments = listOf()),
        NestJSSchematic(name = "module", description = "Generate a module declaration.", options = supportedGenerateSchematicOptions, arguments = listOf()),
        NestJSSchematic(name = "pipe", description = "Generate a pipe declaration.", options = supportedGenerateSchematicOptions, arguments = listOf()),
        NestJSSchematic(name = "provider", description = "Generate a provider declaration.", options = supportedGenerateSchematicOptions, arguments = listOf()),
        NestJSSchematic(name = "resolver", description = "Generate a resolver declaration.", options = supportedGenerateSchematicOptions, arguments = listOf()),
        NestJSSchematic(name = "service", description = "Generate a service declaration.", options = supportedGenerateSchematicOptions, arguments = listOf()),
    )
}