package com.github.coconutmkii.nestjsidea.util

import com.intellij.openapi.roots.ContentEntry
import com.intellij.openapi.vfs.VirtualFile

private val NESTJS_JSON_NAMES = listOf("nest-cli.json", ".nest-cli.json")
private val NEST_JS_EXCLUDES = listOf("dist", "out", "tmp", "coverage", "build")

fun isNestJsonFile(fileName: String): Boolean = NESTJS_JSON_NAMES.contains(fileName)

fun addDefaultNestExcludes(contentEntry: ContentEntry, rootDir: VirtualFile) {
    for (name in NEST_JS_EXCLUDES) {
        val dir = rootDir.findChild(name)
        if (dir != null && dir.isDirectory) {
            contentEntry.addExcludeFolder(dir)
        }
    }
}

fun isProbableLibraryFile(file: VirtualFile): Boolean {
    val path = file.path
    return path.contains("/node_modules/")
            || path.contains("/bower_components/")
            || path.contains("/out/")
            || path.contains("/dist/")
            || path.contains("/build/")
            || path.contains("/.yarn/")
}
