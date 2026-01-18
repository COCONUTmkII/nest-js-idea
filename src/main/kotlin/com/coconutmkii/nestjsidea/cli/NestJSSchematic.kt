package com.coconutmkii.nestjsidea.cli

data class NestJSSchematic(
    val name: String,
    val description: String,
    val options: List<String>,
    val arguments: List<String>,
)
